//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.misc.helpers.AbstractRootWordAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.military.Weapon;

/**
 * This class attempts to identify generically described weapons by first looking for
 * a weapon type (e.g. rifle, grenade) and then extending the entity to
 * include descriptive words (e.g. assault) beforehand.
 */
public class GenericWeapon extends AbstractRootWordAnnotator<Weapon> {

	protected static final List<String> FIREARM = Arrays.asList("firearm", "gun", "handgun", "pistol", "revolver", "rifle", "sidearm", "shotgun");
	protected static final List<String> AMMUNITION = Arrays.asList("ammo", "ammunition", "bullet", "cartridge", "magazine", "round", "shell");
	protected static final List<String> EXPLOSIVE = Arrays.asList("airstrike", "artillery", "bomb", "explosive", "grenade", "ied", "missile", "mortar", "ordnance", "rocket", "rpg", "torpedo", "vbied");
	protected static final List<String> BLADED = Arrays.asList("axe", "blade", "dagger", "knife", "knive", "machete", "sword");
	protected static final List<String> OTHER = Arrays.asList("armament", "flamethrower", "munition", "weapon", "wmd");	
	
	protected static final List<String> DESCRIPTORS = Arrays.asList(
		"chemical", "biological", "nuclear", "atomic", "sonic", "laser",
		"rocket", "propelled", "rail", "air", "ground", "surface",
		"dirty", "improvised", "explosive", "unexploded",
		"lethal", "less-lethal", "non-lethal",
		"tactical", "combat", "commando", "recoilless", "silenced",
		"anti", "aircraft", "tank", "ship", "submarine", "antiaircraft", "anti-aircraft", "antitank", "anti-tank", "antiship", "anti-ship", "antisubmarine", "anti-submarine",
		"sniper", "machine", "assault", "submachine", "sub-machine"
	);
	
	@Override
	protected String isEntity(String word) {
		String ret = null;
		String singular = word;
		if(word.endsWith("s")){
			singular = singular.substring(0,  singular.length() - 1);
		}
		
		if(FIREARM.contains(singular)){
			ret = "FIREARM";
		}else if(AMMUNITION.contains(singular)){
			ret = "AMMUNITION";
		}else if(EXPLOSIVE.contains(singular)){
			ret = "EXPLOSIVE";
		}else if(BLADED.contains(singular)){
			ret = "BLADED";
		}else if(OTHER.contains(singular)){
			ret = "OTHER";
		}
		
		return ret;
	}

	@Override
	protected boolean isDescriptiveWord(String word) {
		return DESCRIPTORS.contains(word);
	}

	@Override
	protected Weapon createEntity(JCas jCas) {
		return new Weapon(jCas);
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Sentence.class, WordToken.class), ImmutableSet.of(Weapon.class));
	}
}