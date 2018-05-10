// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.Set;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.util.DefaultCandidate;
import uk.gov.dstl.baleen.types.semantic.Entity;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDBPediaSupplierTest<T extends Entity> {

  @Mock DBPediaService mockService;

  @Mock EntityInformation<T> mockEntityInformation;

  Set<DefaultCandidate> createMockCandidates() {
    Set<DefaultCandidate> mockCandidates = new HashSet<>();
    DefaultCandidate mockCandidate1 = mock(DefaultCandidate.class);
    mockCandidates.add(mockCandidate1);
    DefaultCandidate mockCandidate2 = mock(DefaultCandidate.class);
    mockCandidates.add(mockCandidate2);
    return mockCandidates;
  }
}
