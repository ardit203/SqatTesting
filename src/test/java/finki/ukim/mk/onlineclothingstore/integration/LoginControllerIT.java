package finki.ukim.mk.onlineclothingstore.integration;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void LC_01_getLoginPage_noErrorParam_returnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void LC_02_getLoginPage_errorParam_readsSessionError_setsModel_andRemovesSessionAttr() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("LOGIN_ERROR", "Invalid username or password!");

        MvcResult res = mockMvc.perform(get("/login")
                        .param("error", "true")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("error"))
                .andReturn();


        Object error = res.getModelAndView().getModel().get("error");
        assertThat(error).isEqualTo("Invalid username or password!");


        HttpSession after = res.getRequest().getSession(false);
        assertThat(after).isNotNull();
        assertThat(after.getAttribute("LOGIN_ERROR")).isNull();
    }

    @Test
    void LC_03_getLoginPage_errorParam_butNoSessionError_noModelAttribute() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/login")
                        .param("error", "true")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("error"));
    }

}
