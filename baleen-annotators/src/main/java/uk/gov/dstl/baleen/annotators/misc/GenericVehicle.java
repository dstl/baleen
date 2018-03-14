// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.misc.helpers.AbstractRootWordAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Vehicle;

/**
 * This class attempts to identify generically described vehicles by first looking for a vehicle
 * type (e.g. car, motorbike, boat) and then extending the entity to include descriptive words (e.g.
 * red, old, rusty) beforehand.
 *
 * <p>Does not include military vehicles.
 *
 * @baleen.javadoc
 */
public class GenericVehicle extends AbstractRootWordAnnotator<Vehicle> {

  protected static final List<String> ROAD_VEHICLE =
      Arrays.asList(
          "car",
          "motorcar",
          "hatchback",
          "bike",
          "bicycle",
          "motorbike",
          "motorcycle",
          "motorbicycle",
          "tricycle",
          "trike",
          "quadbike",
          "van",
          "truck",
          "lorry",
          "tanker",
          "bus",
          "taxi",
          "jeep",
          "4x4",
          "wagon",
          "automobile",
          "cab",
          "taxicab",
          "minibus",
          "coach",
          "digger",
          "bulldozer",
          "engine",
          "ambulance");
  protected static final List<String> RAIL_VEHICLE =
      Arrays.asList(
          "carriage",
          "locomotive",
          "tram",
          "tramcar",
          "streetcar"); // train not included as it is more often used to refer to training
  protected static final List<String> MARITIME_VEHICLE =
      Arrays.asList(
          "ship",
          "boat",
          "vessel",
          "ferry",
          "canoe",
          "kayak",
          "dinghy",
          "yacht",
          "lifeboat",
          "barge",
          "trawler",
          "tug",
          "pedalo",
          "raft",
          "motorboat",
          "motorship");
  protected static final List<String> AIR_VEHICLE =
      Arrays.asList(
          "aircraft",
          "airliner",
          "plane",
          "aeroplane",
          "airplane",
          "biplane",
          "monoplane",
          "helicopter",
          "glider",
          "parachute",
          "jet",
          "jetpack",
          "balloon",
          "airship",
          "blimp",
          "dirigible",
          "ultralight",
          "hangglider",
          "floatplane",
          "autogyro");
  protected static final List<String> SPACE_VEHICLE =
      Arrays.asList("spacecraft", "spaceship", "satellite", "rover");
  protected static final List<String> OTHER_VEHICLE =
      Arrays.asList("vehicle", "hovercraft", "skidoo");

  protected static final List<String> DESCRIPTORS =
      Arrays.asList(
          // Vehicle type qualifiers
          "pickup",
          "articulated",
          "jumbo",
          "hire",
          "pool",
          "wheeled",
          "tracked",
          "passenger",
          "cargo",
          "research",
          "cruise",
          "container",
          "commercial",
          "civilian",
          "escort",
          "camper",
          "postal",
          "space",
          "interstellar",
          "planetary",
          "robotic",
          "semi",
          "safety",
          "dumper",
          "police",
          "fire",
          // Propulsion
          "motor",
          "motorised",
          "sail",
          "sailing",
          "nuclear",
          "diesel",
          "petrol",
          "gas",
          "hybrid",
          "steam",
          "electric",
          "solar",
          "powered",
          "rowing",
          // Descriptive words
          "old",
          "new",
          "big",
          "small",
          "rusty",
          "dusty",
          "dirty",
          "clean",
          "fast",
          "slow",
          "metal",
          "plastic",
          "wooden",
          "branded",
          "unbranded",
          "painted",
          "sports",
          "hot",
          "cold",
          "float",
          "floating",
          "submersible",
          "narrow",
          "thin",
          "wide",
          "long",
          "short",
          "inflatable",
          "dangerous",
          "unsafe",
          "safe",
          "decommissioned",
          // Colors
          "aqua",
          "aquamarine",
          "azure",
          "beige",
          "bisque",
          "black",
          "blue",
          "bronze",
          "brown",
          "chocolate",
          "copper",
          "coral",
          "crimson",
          "cyan",
          "forest",
          "fuchsia",
          "gold",
          "golden",
          "gray",
          "green",
          "grey",
          "indigo",
          "ivory",
          "khaki",
          "lavender",
          "lemon",
          "lime",
          "magenta",
          "maroon",
          "mint",
          "navy",
          "olive",
          "orange",
          "orchid",
          "peach",
          "pink",
          "plum",
          "purple",
          "red",
          "salmon",
          "silver",
          "tan",
          "teal",
          "turquoise",
          "violet",
          "white",
          "yellow",
          "dark",
          "dim",
          "light",
          "pale",
          "metallic");

  @Override
  protected String isEntity(String word) {
    String ret = null;

    String singular = word;
    if (word.endsWith("s")) {
      singular = singular.substring(0, singular.length() - 1);
    }

    if (ROAD_VEHICLE.contains(singular)) {
      ret = "ROAD";
    } else if (RAIL_VEHICLE.contains(singular)) {
      ret = "RAIL";
    } else if (MARITIME_VEHICLE.contains(singular)) {
      ret = "MARITIME";
    } else if (AIR_VEHICLE.contains(singular)) {
      ret = "AIR";
    } else if (SPACE_VEHICLE.contains(singular)) {
      ret = "SPACE";
    } else if (OTHER_VEHICLE.contains(singular)) {
      ret = "OTHER";
    }

    return ret;
  }

  @Override
  protected boolean isDescriptiveWord(String word) {
    return DESCRIPTORS.contains(word);
  }

  @Override
  protected Vehicle createEntity(JCas jCas) {
    return new Vehicle(jCas);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(requiredInputs, ImmutableSet.of(Vehicle.class));
  }
}
