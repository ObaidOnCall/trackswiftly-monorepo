package com.trackswiftly.vehicle_service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.trackswiftly.vehicle_service.dao.repositories.VehicleTypeRepo;
import com.trackswiftly.vehicle_service.entities.VehicleType;
import com.trackswiftly.vehicle_service.utils.DBUtiles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;


@DisabledInNativeImage
class VehicleTypeRepoTest {
    
    
    @Mock
    private EntityManager em;
    
    @InjectMocks
    private VehicleTypeRepo vehicleTypeRepo;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(vehicleTypeRepo, "batchSize", 3);
    }


    @Test
    void testInsertInBatch() {
        List<VehicleType> entities = List.of(new VehicleType(), new VehicleType() , new VehicleType());

        vehicleTypeRepo.insertInBatch(entities);

        verify(em, times(3)).persist(any(VehicleType.class));

        verify(em, times(1)).flush();
        verify(em, times(1)).clear();
    }

    @Test
    void testUpdateInBatch() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        VehicleType vehicleType = new VehicleType();

        try (MockedStatic<DBUtiles> utilities = Mockito.mockStatic(DBUtiles.class)) {
            Query mockQuery = mock(Query.class);
            utilities.when(() -> DBUtiles.buildJPQLQueryDynamicallyForUpdate(vehicleType, em)).thenReturn(mockQuery);
            when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
            when(mockQuery.executeUpdate()).thenReturn(1);

            int updatedRecords = vehicleTypeRepo.updateInBatch(ids, vehicleType);

            assertEquals(1, updatedRecords);
            verify(mockQuery, times(1)).setParameter("Ids", ids);
            verify(mockQuery, times(1)).executeUpdate();
            utilities.verify(() -> DBUtiles.buildJPQLQueryDynamicallyForUpdate(vehicleType, em), times(1));
        }
    }




    @Test
    void testDeleteByIds_NullOrEmptyIds() {
        // Test with null
        int result = vehicleTypeRepo.deleteByIds(null);
        assertEquals(0, result);

        // Test with empty list
        result = vehicleTypeRepo.deleteByIds(Collections.emptyList());
        assertEquals(0, result);

        // Verify that no interactions with EntityManager occurred
        verifyNoInteractions(em);
    }


    @Test
    void testDeleteByIds_ValidIds() {
        List<Long> ids = List.of(1L, 2L, 3L);

        Query mockQuery = mock(Query.class);

        when(em.createQuery("DELETE FROM VehicleType d WHERE d.id IN :ids")).thenReturn(mockQuery);
        when(mockQuery.setParameter("ids", ids)).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(ids.size());

        int result = vehicleTypeRepo.deleteByIds(ids);

        assertEquals(ids.size(), result);

        verify(em).createQuery("DELETE FROM VehicleType d WHERE d.id IN :ids");
        verify(mockQuery).setParameter("ids", ids);
        verify(mockQuery).executeUpdate();
    }



    @Test
    void testFindByIds_NullOrEmptyIds() {
        // Test with null
        List<VehicleType> result = vehicleTypeRepo.findByIds(null);
        assertTrue(result.isEmpty());

        // Test with empty list
        result = vehicleTypeRepo.findByIds(Collections.emptyList());
        assertTrue(result.isEmpty());

        // Verify that no interactions with EntityManager occurred
        verifyNoInteractions(em);
    }




    @Test
    void testFindByIds_ValidIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<VehicleType> expectedVehicleTypes = List.of(new VehicleType(), new VehicleType(), new VehicleType());

        @SuppressWarnings("unchecked")
        TypedQuery<VehicleType> mockQuery = mock(TypedQuery.class);

        when(em.createQuery("SELECT d FROM VehicleType d WHERE d.id IN :ids", VehicleType.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("ids", ids)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedVehicleTypes);

        List<VehicleType> result = vehicleTypeRepo.findByIds(ids);

        assertEquals(expectedVehicleTypes.size(), result.size());
        assertEquals(expectedVehicleTypes, result);

        verify(em).createQuery("SELECT d FROM VehicleType d WHERE d.id IN :ids", VehicleType.class);
        verify(mockQuery).setParameter("ids", ids);
        verify(mockQuery).getResultList();
    }


}
