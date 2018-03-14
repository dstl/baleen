// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;

import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.military.Weapon;

public class TestWeapon extends TestAnnotation<Weapon> {

  private final String weaponType;

  public TestWeapon(int index, String text, String weaponType) {
    super(index, text);
    this.weaponType = weaponType;
  }

  @Override
  public void validate(Weapon t) {
    super.validate(t);

    assertEquals(weaponType, t.getSubType());
  }
}
