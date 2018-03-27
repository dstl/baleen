// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.mallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.KEYWORDS_FIELD;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.KEY_STOPWORDS;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.PARAM_MODEL_FILE;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.PARAM_NUMBER_OF_ITERATIONS;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.PARAM_NUMBER_OF_TOPICS;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.TOPIC_FIELD;
import static uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer.TOPIC_NUMBER_FIELD;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.IDSorter;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.uima.AbstractBaleenTaskTest;

public class TopicModelTrainerTest extends AbstractBaleenTaskTest {

  private static final int NUM_TOPICS = 5;
  private static final int NUM_ITERATIONS = 10;
  private static final String SOURCE = "source";
  private static final String CONTENT = "content";
  private static final String COLLECTION = "documents";
  private Path modelPath;
  private MongoCollection<Document> documents;

  @Before
  public void before()
      throws URISyntaxException, ResourceInitializationException, AnalysisEngineProcessException,
          ResourceAccessException {

    ExternalResourceDescription stopWordsErd =
        ExternalResourceFactory.createExternalResourceDescription(
            TopicModelTrainer.KEY_STOPWORDS, SharedStopwordResource.class);

    // @formatter:off
    ImmutableList<String> data =
        ImmutableList.of(
            new Document()
                .append(SOURCE, "elizabeth_needham.txt")
                .append(
                    CONTENT,
                    "Elizabeth Needham (died 3 May 1731), also known as Mother Needham, was an English procuress and brothel-keeper of 18th-century London, who has been identified as the bawd greeting Moll Hackabout in the first plate of William Hogarth's series of satirical etchings, A Harlot's Progress. Although Needham was notorious in London at the time, little is recorded of her life, and no genuine portraits of her survive. Her house was the most exclusive in London and her customers came from the highest strata of fashionable society, but she eventually crossed the moral reformers of the day and died as a result of the severe treatment she received after being sentenced to stand in the pillory.")
                .toJson(),
            new Document()
                .append(SOURCE, "equipartition_theorem.txt")
                .append(
                    CONTENT,
                    "The equipartition theorem is a formula from statistical mechanics that relates the temperature of a system with its average energies. The original idea of equipartition was that, in thermal equilibrium, energy is shared equally among its various forms; for example, the average kinetic energy in the translational motion of a molecule should equal the average kinetic energy in its rotational motion. Like the virial theorem, the equipartition theorem gives the total average kinetic and potential energies for a system at a given temperature, from which the system's heat capacity can be computed. However, equipartition also gives the average values of individual components of the energy. It can be applied to any classical system in thermal equilibrium, no matter how complicated. The equipartition theorem can be used to derive the classical ideal gas law, and the Dulong–Petit law for the specific heat capacities of solids. It can also be used to predict the properties of stars, even white dwarfs and neutron stars, since it holds even when relativistic effects are considered. Although the equipartition theorem makes very accurate predictions in certain conditions, it becomes inaccurate when quantum effects are significant, namely at low enough temperatures.")
                .toJson(),
            new Document()
                .append(SOURCE, "gunnhild.txt")
                .append(
                    CONTENT,
                    "Gunnhild konungamóðir (mother of kings) or Gunnhild Gormsdóttir[1] (c. 910  –  c. 980) was the wife of Erik Bloodaxe (king of Norway 930–34, \"king\" of Orkney c. 937–54, and king of Jórvík 948–49 and 952–54). Gunnhild is a prominent figure in many Norse sagas, including Fagrskinna, Egil's Saga, Njal's Saga, and Heimskringla. Many of the details of her life are disputed, including her parentage. Gunnhild lived during a time of great change in Norway. Her father-in-law Harald Fairhair had recently united much of Norway under his rule. Shortly after his death, Gunnhild and her husband were overthrown and exiled. She spent much of the rest of her life in exile in Orkney, Jorvik and Denmark. A number of her many children with Erik became co-rulers of Norway in the late tenth century. What details of her life are known come largely from Icelandic sources; because the Icelanders were generally hostile to her and her husband, scholars regard some of the episodes reported in them as suspect.")
                .toJson(),
            new Document()
                .append(SOURCE, "hawes.txt")
                .append(
                    CONTENT,
                    "Richard Hawes (1797–1877) was a United States Representative from Kentucky and the second Confederate Governor of Kentucky. Originally a Whig, Hawes became a Democrat following the dissolution of the Whig party in the 1850s. At the outbreak of the American Civil War, Hawes was a supporter of Kentucky's doctrine of armed neutrality. When the Commonwealth's neutrality was breached in September 1861, Hawes fled to Virginia and enlisted as a brigade commissary under Confederate general Humphrey Marshall. He was elected Confederate governor of the Commonwealth following the late George W. Johnson's death at the Battle of Shiloh. Hawes and the Confederate government traveled with Braxton Bragg's Army of Tennessee, and when Bragg invaded Kentucky in October 1862, he captured Frankfort and held an inauguration ceremony for Hawes. The ceremony was interrupted, however, by forces under Union general Don Carlos Buell, and the Confederates were driven from the Commonwealth following the Battle of Perryville. Hawes relocated to Virginia, where he continued to lobby President Jefferson Davis to attempt another invasion of Kentucky. Following the war, he returned to his home in Paris, Kentucky, swore an oath of allegiance to the Union, and was allowed to return to his law practice.")
                .toJson(),
            new Document()
                .append(SOURCE, "hill.txt")
                .append(
                    CONTENT,
                    "Clem Hill (1877–1945) was an Australian cricketer who played 49 Test matches as a specialist batsman between 1896 and 1912. He captained the Australian team in ten Tests, winning five and losing five. A prolific run scorer, Hill scored 3,412 runs in Test cricket—a world record at the time of his retirement—at an average of 39.21 per innings, including seven centuries. In 1902, Hill was the first batsman to make 1,000 Test runs in a calendar year, a feat that would not be repeated for 45 years. His innings of 365 scored against New South Wales for South Australia in 1900–01 was a Sheffield Shield record for 27 years. His Test cricket career ended in controversy after he was involved in a brawl with cricket administrator and fellow Test selector Peter McAlister in 1912. He was one of the \"Big Six\", a group of leading Australian cricketers who boycotted the 1912 Triangular Tournament in England when the players were stripped of the right to appoint the tour manager. The boycott effectively ended his Test career. After retiring from cricket, Hill worked in the horse racing industry as a stipendiary steward and later as a handicapper for races including the Caulfield Cup.")
                .toJson(),
            new Document()
                .append(SOURCE, "shiloh.txt")
                .append(
                    CONTENT,
                    "The Battle of Shiloh, also known as the Battle of Pittsburg Landing, was a major battle in the Western Theater of the American Civil War, fought on April 6 and April 7, 1862, in southwestern Tennessee. Confederate forces under Generals Albert Sidney Johnston and P.G.T. Beauregard launched a surprise attack against the Union Army of Maj. Gen. Ulysses S. Grant and came very close to defeating his army. On the first day of battle, the Confederates struck with the intention of driving the Union defenders away from the Tennessee River and into the swamps of Owl Creek to the west, hoping to defeat Grant's Army of the Tennessee before it could link up with Maj. Gen. Don Carlos Buell's Army of the Ohio. The Confederate battle lines became confused during the fierce fighting, and Grant's men instead fell back in the direction of Pittsburg Landing to the northeast. A position on a slightly sunken road, nicknamed the \"Hornet's Nest\", defended by the men of Brig. Gens. Benjamin M. Prentiss's and W.H.L. Wallace's divisions, provided critical time for the rest of the Union line to stabilize under the protection of numerous artillery batteries. Gen. Johnston was killed during the first day's fighting, and Beauregard, his second in command, decided against assaulting the final Union position that night. Reinforcements from Gen. Buell arrived in the evening and turned the tide the next morning, when he and Grant launched a counterattack along the entire line. The Confederates were forced to retreat from the bloodiest battle in United States history up to that time, ending their hopes that they could block the Union advance into northern Mississippi.")
                .toJson(),
            new Document()
                .append(SOURCE, "sunderland_echo.txt")
                .append(
                    CONTENT,
                    "The Sunderland Echo is an evening provincial newspaper serving the Sunderland, South Tyneside and East Durham areas of North East England. The newspaper was founded by Samuel Storey, Edward Backhouse, Edward Temperley Gourley, Charles Palmer, Richard Ruddock, Thomas Glaholm and Thomas Scott Turnbull in 1873, as the Sunderland Daily Echo and Shipping Gazette. Designed to provide a platform for the Radical views held by Storey and his partners, it was also Sunderland's first local daily paper. The inaugural edition of the Echo was printed in Press Lane, Sunderland on 22 December 1873; 1,000 copies were produced and sold for a halfpenny each. The Echo survived intense competition in its early years, as well as the depression of the 1930s and two World Wars. Sunderland was heavily bombed in the Second World War and, although the Echo building was undamaged, it was forced to print its competitor's paper under wartime rules. It was during this time that the paper's format changed, from a broadsheet to its current tabloid layout, because of national newsprint shortages.")
                .toJson(),
            new Document()
                .append(SOURCE, "thespis.txt")
                .append(
                    CONTENT,
                    "Thespis is an operatic extravaganza that was the first collaboration between dramatist W. S. Gilbert and composer Arthur Sullivan. It was never published, and most of the music is now lost. However, Gilbert and Sullivan went on to become one of the most famous and successful partnerships in Victorian England, creating a string of comic opera hits, including H.M.S. Pinafore, The Pirates of Penzance and The Mikado, that continue to be popular. Thespis premièred in London at the Gaiety Theatre on 26 December 1871. Like many productions at that theatre, it was written in a broad, burlesque style, considerably different from Gilbert and Sullivan's later works. It was a modest success—for a Christmas entertainment of the time—and closed on 8 March 1872, after a run of 63 performances. It was advertised as \"An entirely original Grotesque Opera in Two Acts\". The story follows an acting troupe headed by Thespis, the legendary Greek father of the drama, who temporarily trade places with the gods on Mount Olympus, who have grown elderly and ignored. The actors turn out to be comically inept rulers. Having seen the ensuing mayhem down below, the angry gods return, sending the actors back to Earth as \"eminent tragedians, whom no one ever goes to see.\"")
                .toJson(),
            new Document()
                .append(SOURCE, "thylacine.txt")
                .append(
                    CONTENT,
                    "The Thylacine (pronounced /ˈθaɪləsaɪn/) (Thylacinus cynocephalus, Greek: dog-headed pouched one) was the largest known carnivorous marsupial of modern times. Native to continental Australia, Tasmania and New Guinea, it is thought to have become extinct in the 20th century. It is commonly known as the Tasmanian Tiger (because of its striped back), the Tasmanian Wolf, and colloquially the Tassie (or Tazzy) Tiger or simply the Tiger.[3] It was the last extant member of its genus, Thylacinus, although several related species have been found in the fossil record dating back to the early Miocene. The Thylacine became extinct on the Australian mainland thousands of years before European settlement of the continent, but it survived on the island of Tasmania along with several endemic species, including the Tasmanian Devil. Intensive hunting encouraged by bounties is generally blamed for its extinction, but other contributory factors may have been disease, the introduction of dogs, and human encroachment into its habitat. Despite it being officially classified as extinct, sightings are still reported. Like the tigers and wolves of the Northern Hemisphere, from which it obtained two of its common names, the Thylacine was an apex predator. As a marsupial, it was not related to these placental mammals, but because of convergent evolution it displayed the same general form and adaptations. Its closest living relative is the Tasmanian Devil. The Thylacine was one of only two marsupials to have a pouch in both sexes (the other is the Water Opossum). The male Thylacine had a pouch that acted as a protective sheath, protecting the male's external reproductive organs while running through thick brush.")
                .toJson(),
            new Document()
                .append(SOURCE, "uranus.txt")
                .append(
                    CONTENT,
                    "The rings of Uranus were discovered on March 10, 1977, by James L. Elliot, Edward W. Dunham, and Douglas J. Mink. Two additional rings were discovered in 1986 by the Voyager 2 spacecraft, and two outer rings were found in 2003–2005 by the Hubble Space Telescope. A number of faint dust bands and incomplete arcs may exist between the main rings. The rings are extremely dark—the Bond albedo of the rings' particles does not exceed 2%. They are likely composed of water ice with the addition of some dark radiation-processed organics. The majority of Uranus's rings are opaque and only a few kilometres wide. The ring system contains little dust overall; it consists mostly of large bodies 0.2–20 m in diameter. The relative lack of dust in the ring system is due to aerodynamic drag from the extended Uranian exosphere—corona. The rings of Uranus are thought to be relatively young, at not more than 600 million years. The mechanism that confines the narrow rings is not well understood. The Uranian ring system probably originated from the collisional fragmentation of a number of moons that once existed around the planet. After colliding, the moons broke up into numerous particles, which survived as narrow and optically dense rings only in strictly confined zones of maximum stability.")
                .toJson(),
            new Document()
                .append(SOURCE, "yard.txt")
                .append(
                    CONTENT,
                    "Robert Sterling Yard (1861–1945) was an American writer, journalist and wilderness activist. Yard graduated from Princeton University and spent the first twenty years of his career as a journalist, editor and publisher. In 1915 he was recruited by his friend Stephen Mather to help publicize the need for an independent national park agency. Their numerous publications were part of a movement that resulted in legislative support for a National Park Service in 1916. Yard served as head of the National Parks Educational Committee for several years after its conception, but tension within the NPS led him to concentrate on non-government initiatives. He became executive secretary of the National Parks Association in 1919. Yard worked to promote the national parks as well as educate Americans about their use. Creating high standards based on aesthetic ideals for park selection, he also opposed commercialism and industrialization of what he called \"America's masterpieces\". These standards caused discord with his peers. After helping to establish a relationship between the NPA and the United States Forest Service, Yard later became involved in the protection of wilderness areas. In 1935 he became one of the eight founding members of The Wilderness Society and acted as its first president from 1937 until his death eight years later. Yard is now considered an important figure in the modern wilderness movement.")
                .toJson(),
            new Document()
                .append(SOURCE, "zinta.txt")
                .append(
                    CONTENT,
                    "Preity Zinta (born 1975) is an Indian film actress. She has appeared in Hindi films of Bollywood, as well as Telugu and English-language movies. After graduating with a degree in criminal psychology, Zinta made her acting debut in Dil Se in 1998 followed by a role in Soldier the same year. These performances earned her a Filmfare Best Female Debut Award, and she was later recognised for her role as a teenage single mother in Kya Kehna (2000). She subsequently played a variety of character types, and in doing so has been credited with changing the image of a Hindi film heroine. Zinta received her first Filmfare Best Actress Award in 2003 for her performance in the drama Kal Ho Naa Ho. She went on to play the lead female role in two consecutive annual top-grossing films in India: the science fiction film Koi... Mil Gaya, her biggest commercial success, and the star-crossed romance Veer-Zaara, which earned her critical acclaim. She was later noted for her portrayal of independent, modern Indian women in Salaam Namaste and Kabhi Alvida Naa Kehna, top-grossing productions in overseas markets. These accomplishments have established her as a leading actress of Hindi cinema. In addition to movie acting, Zinta has written a series of columns for BBC News Online South Asia, is a regular stage performer, and along with boyfriend Ness Wadia she is a co-owner of the Indian Premier League cricket team Kings XI Punjab.")
                .toJson());

    // @formatter:on

    try {
      modelPath = Files.createTempFile("model", ".mallet");
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }

    ExternalResourceDescription fongoErd =
        ExternalResourceFactory.createExternalResourceDescription(
            SharedMongoResource.RESOURCE_KEY,
            SharedFongoResource.class,
            "fongo.collection",
            COLLECTION,
            "fongo.data",
            data.toString());

    final AnalysisEngine ae =
        create(
            TopicModelTrainer.class,
            KEY_STOPWORDS,
            stopWordsErd,
            SharedMongoResource.RESOURCE_KEY,
            fongoErd,
            PARAM_NUMBER_OF_TOPICS,
            NUM_TOPICS,
            PARAM_NUMBER_OF_ITERATIONS,
            NUM_ITERATIONS,
            PARAM_MODEL_FILE,
            modelPath.toString());

    execute(ae);

    SharedFongoResource sfr =
        (SharedFongoResource)
            ae.getUimaContext().getResourceObject(SharedMongoResource.RESOURCE_KEY);
    documents = sfr.getDB().getCollection(COLLECTION);
  }

  @Test
  public void testTaskProducesValidModelFile() throws Exception {

    File modelFile = modelPath.toFile();
    assertTrue(modelFile.exists());

    ParallelTopicModel model = ParallelTopicModel.read(modelFile);
    assertEquals(NUM_TOPICS, model.getNumTopics());

    // Sanity check the Mallet code does something, but no need to test it.
    double[] topicDistribution = model.getTopicProbabilities(0);
    ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
    Formatter out = new Formatter(new StringBuilder(), Locale.UK);
    for (int topic = 0; topic < NUM_TOPICS; topic++) {
      out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
      int rank = 0;
      Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
      while (iterator.hasNext() && rank < 5) {
        IDSorter idCountPair = iterator.next();
        out.format(
            "%s (%.0f) ",
            model.alphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
        rank++;
      }
      out.format("\n");
    }
    System.out.println(out);

    FindIterable<Document> find = documents.find();
    MongoCursor<Document> iterator = find.iterator();
    int count = 0;
    while (iterator.hasNext()) {
      Document document = iterator.next();
      Document topic = (Document) document.get(TOPIC_FIELD);
      assertNotNull(topic.getString(KEYWORDS_FIELD));
      assertNotNull(topic.getInteger(TOPIC_NUMBER_FIELD));
      count++;
    }

    assertEquals(12, count);
  }
}
