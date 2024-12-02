package org.smg.carlisting.presentation;

import org.junit.jupiter.api.Test;
import org.smg.carlisting.application.CarListingSearchCriteria;
import org.smg.carlisting.application.CarListingService;
import org.smg.carlisting.domain.model.CarListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarListingController.class)
public class CarListingControllerTest {

    private static final String URL_SEARCH = "/api/car-listings/search";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarListingService carListingService;

    @Test
    public void testSearchCarListingsWithParams() throws Exception {
        List<CarListing> expectedListings = Arrays.asList(
                new CarListing("1", "Peugeot", "308", 2015, 10000, 12000, "black"),
                new CarListing("2", "Peugeot", "307", 2015, 9000, 12000, "blue")
        );

        when(carListingService.searchCarListings(any(CarListingSearchCriteria.class), any(Pageable.class)))
                .thenReturn(expectedListings);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_SEARCH)
                        .param("make", "Peugeot")
                        .param("model", "308")
                        .param("year", "2015")
                        .param("minPrice", "10000")
                        .param("maxPrice", "12000")
                        .param("color", "black"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.valid", is(true)))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].make", is("Peugeot")))
                .andExpect(jsonPath("$.data[0].model", is("308")))
                .andExpect(jsonPath("$.data[0].year", is(2015)))
                .andExpect(jsonPath("$.data[0].minPrice", is(10000.0)))
                .andExpect(jsonPath("$.data[0].maxPrice", is(12000.0)))
                .andExpect(jsonPath("$.data[0].color", is("black")))
                .andExpect(jsonPath("$.data[1].make", is("Peugeot")))
                .andExpect(jsonPath("$.data[1].model", is("307")))
                .andExpect(jsonPath("$.data[1].year", is(2015)))
                .andExpect(jsonPath("$.data[1].minPrice", is(9000.0)))
                .andExpect(jsonPath("$.data[1].maxPrice", is(12000.0)))
                .andExpect(jsonPath("$.data[1].color", is("blue")));

        verify(carListingService).searchCarListings(any(CarListingSearchCriteria.class), any(Pageable.class));
    }

    @Test
    public void testSearchCarListingsWithoutParams() throws Exception {
        when(carListingService.searchCarListings(any(CarListingSearchCriteria.class), any(Pageable.class)))
                .thenReturn(List.of(new CarListing()));

        mockMvc.perform(MockMvcRequestBuilders.get(URL_SEARCH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray());

        verify(carListingService).searchCarListings(any(CarListingSearchCriteria.class), any(Pageable.class));
    }

    @Test
    public void testSearchCarListingsInvalidParams() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_SEARCH)
                        .param("make", "Peugeot")
                        .param("model", "308")
                        .param("year", "3000")
                        .param("minPrice", "10000")
                        .param("maxPrice", "12000")
                        .param("color", "black"))
                .andExpect(status().isBadRequest());

        verify(carListingService, never()).searchCarListings(any(CarListingSearchCriteria.class), any(Pageable.class));
    }
}
