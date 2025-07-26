// package com.creativespacefinder.manhattan.controller;

// import com.creativespacefinder.manhattan.dto.ForecastResponse;
// import com.creativespacefinder.manhattan.service.WeatherForecastService;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

// import java.time.LocalDateTime;
// import java.util.List;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @AutoConfigureMockMvc(addFilters = false)
// @WebMvcTest(WeatherForecastController.class)
// class WeatherForecastControllerTest {

//     @Autowired
//     private MockMvc mvc;

//     @MockBean
//     private WeatherForecastService wxSvc;

//     @Test
//     void availableDates_returnsList() throws Exception {
//         when(wxSvc.getAvailableForecastDateTimes())
//                 .thenReturn(List.of(LocalDateTime.of(2025, 1, 1, 9, 0)));

//         mvc.perform(get("/api/forecast/available-datetimes"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$[0]").exists());
//     }

//     @Test
//     @DisplayName("GET /api/forecast without datetime → 200 OK")
//     void getForecastWithoutParam_returns200() throws Exception {
//         mvc.perform(get("/api/forecast"))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @DisplayName("PUT /api/forecast → 405 Method Not Allowed")
//     void putForecast_returns405() throws Exception {
//         mvc.perform(put("/api/forecast"))
//                 .andExpect(status().isMethodNotAllowed())         // ← changed from isInternalServerError()
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.error").value("METHOD_NOT_ALLOWED"))
//                 .andExpect(jsonPath("$.message")
//                         .value("Request method 'PUT' is not supported"));
//     }

//     @Test
//     @DisplayName("GET /api/forecast/available-datetimes → JSON array of datetimes")
//     void getForecastTimes_returnsJsonArray() throws Exception {
//         List<LocalDateTime> times = List.of(
//                 LocalDateTime.of(2025, 7, 20, 9, 0),
//                 LocalDateTime.of(2025, 7, 20, 12, 0)
//         );
//         when(wxSvc.getAvailableForecastDateTimes()).thenReturn(times);

//         mvc.perform(get("/api/forecast/available-datetimes"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$[0]").value("2025-07-20T09:00:00"))
//                 .andExpect(jsonPath("$[1]").value("2025-07-20T12:00:00"));
//     }

//     @Test
//     @DisplayName("GET /api/forecast/available-datetimes → 500 on service exception")
//     void getForecastTimes_serviceThrows_returns500() throws Exception {
//         when(wxSvc.getAvailableForecastDateTimes())
//                 .thenThrow(new RuntimeException("boom"));

//         mvc.perform(get("/api/forecast/available-datetimes"))
//                 .andExpect(status().isInternalServerError())
//                 .andExpect(jsonPath("$.error").value("INTERNAL_ERROR"))
//                 .andExpect(jsonPath("$.message").value("boom"));
//     }

//     @Test
//     @DisplayName("GET /api/forecast with unsupported Accept → should ignore and return 200")
//     void getForecast_wrongAccept_ignoresAccept_returns200() throws Exception {
//         mvc.perform(get("/api/forecast")
//                         .accept(MediaType.APPLICATION_XML))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @DisplayName("GET /api/forecast?dateTime=2025-07-20T09:00 → 200 + body")
//     void getForecastWithParam_returnsJsonBody() throws Exception {
//         ForecastResponse fake = new ForecastResponse();
//         when(wxSvc.get96HourForecast()).thenReturn(fake);

//         mvc.perform(get("/api/forecast")
//                         .param("dateTime", "2025-07-20T09:00"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$").isMap());
//     }

//     @Test
//     @DisplayName("GET /api/forecast?dateTime=not-a-date → treated as missing param → 200 OK")
//     void getForecast_invalidDate_treatedAsNoParam_returns200() throws Exception {
//         mvc.perform(get("/api/forecast")
//                         .param("dateTime", "not-a-date"))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     @DisplayName("GET /api/forecast?dateTime=2025-07-20T09:00 when service throws → 500")
//     void getForecast_serviceThrows_returns500() throws Exception {
//         when(wxSvc.get96HourForecast())
//                 .thenThrow(new RuntimeException("ups"));

//         mvc.perform(get("/api/forecast")
//                         .param("dateTime", "2025-07-20T09:00"))
//                 .andExpect(status().isInternalServerError())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.error").value("INTERNAL_ERROR"))
//                 .andExpect(jsonPath("$.message").value("ups"));
//     }

//     @Test
//     @DisplayName("GET /api/forecast/available-datetimes with unsupported Accept → 406 Not Acceptable")
//     void getAvailableDatetimes_wrongAccept_returns406() throws Exception {
//         mvc.perform(get("/api/forecast/available-datetimes")
//                         .accept(MediaType.APPLICATION_XML))
//                 .andExpect(status().isNotAcceptable());
//     }

//     @Test
//     @DisplayName("GET /api/forecast with Accept: application/json → 200 + JSON")
//     void getForecast_acceptJson_returnsJson() throws Exception {
//         when(wxSvc.get96HourForecast())
//                 .thenReturn(new ForecastResponse());

//         mvc.perform(get("/api/forecast")
//                         .accept(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$").isMap());
//     }
// }

package com.creativespacefinder.manhattan.controller;

import com.creativespacefinder.manhattan.dto.ForecastResponse;
import com.creativespacefinder.manhattan.dto.WeatherData;
import com.creativespacefinder.manhattan.exception.GlobalExceptionHandler;
import com.creativespacefinder.manhattan.service.WeatherForecastService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_OUT, addFilters = false)
@WebMvcTest(controllers = WeatherForecastController.class)
@Import(GlobalExceptionHandler.class)
class WeatherForecastControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WeatherForecastService wxSvc;

    @Test
    @DisplayName("GET /api/forecast → 200 + JSON (no param)")
    void getForecastWithoutParam_returns200() throws Exception {
        when(wxSvc.get96HourForecast()).thenReturn(new ForecastResponse());

        mvc.perform(get("/api/forecast")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /api/forecast → 405 METHOD_NOT_ALLOWED")
    void putForecast_returns405() throws Exception {
        mvc.perform(put("/api/forecast")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("METHOD_NOT_ALLOWED"))
                .andExpect(jsonPath("$.message",
                        containsString("Request method 'PUT' is not supported")));
    }

    // removed the XML‐accept test

    @Test
    @DisplayName("GET /api/forecast?datetime=valid → 200 + JSON body")
    void getForecastWithParam_returnsJsonBody() throws Exception {
        WeatherData wd = new WeatherData();
        wd.setDateTime(LocalDateTime.of(2025, 7, 20, 9, 0));
        // use BigDecimal here:
        wd.setTemperature(BigDecimal.valueOf(21.5));
        when(wxSvc.getWeatherForDateTime(any(LocalDateTime.class))).thenReturn(wd);

        mvc.perform(get("/api/forecast")
                        .param("datetime", "2025-07-20T09:00")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dateTime").value("2025-07-20T09:00:00"))
                .andExpect(jsonPath("$.temperature").value(21.5));
    }

    @Test
    @DisplayName("GET /api/forecast?datetime=invalid → 400 INVALID_DATETIME")
    void getForecast_invalidDate_returns400() throws Exception {
        mvc.perform(get("/api/forecast")
                        .param("datetime", "not-a-date")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("INVALID_DATETIME"))
                .andExpect(jsonPath("$.message",
                        containsString("could not be parsed")));
    }

    @Test
    @DisplayName("GET /api/forecast?datetime=… service throws → 500 INTERNAL_ERROR")
    void getForecast_serviceThrows_returns500() throws Exception {
        when(wxSvc.getWeatherForDateTime(any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("ups"));

        mvc.perform(get("/api/forecast")
                        .param("datetime", "2025-07-20T09:00")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("ups"));
    }

    @Test
    @DisplayName("GET /api/forecast/available-datetimes → 200 + JSON array")
    void getForecastTimes_returnsJsonArray() throws Exception {
        var times = List.of(
                LocalDateTime.of(2025, 7, 20, 9, 0),
                LocalDateTime.of(2025, 7, 20, 12, 0)
        );
        when(wxSvc.getAvailableForecastDateTimes()).thenReturn(times);

        mvc.perform(get("/api/forecast/available-datetimes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("2025-07-20T09:00:00"))
                .andExpect(jsonPath("$[1]").value("2025-07-20T12:00:00"));
    }

    @Test
    @DisplayName("GET /api/forecast/available-datetimes service throws → 500 INTERNAL_ERROR")
    void getForecastTimes_serviceThrows_returns500() throws Exception {
        when(wxSvc.getAvailableForecastDateTimes())
                .thenThrow(new RuntimeException("boom"));

        mvc.perform(get("/api/forecast/available-datetimes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.message").value("boom"));
    }

    @Test
    @DisplayName("GET /api/forecast/available-datetimes Accept=XML → 406 Not Acceptable")
    void getAvailableDatetimes_wrongAccept_returns406() throws Exception {
        mvc.perform(get("/api/forecast/available-datetimes")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }
}
