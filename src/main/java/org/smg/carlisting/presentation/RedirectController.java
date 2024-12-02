package org.smg.carlisting.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for redirecting to different documentation pages.
 * <p>
 * This controller provides convenient redirect endpoints to access
 * the Swagger UI and API documentation.
 * </p>
 */
@RestController
public class RedirectController {

    private static final String REDIRECT_SWAGGER_UI_INDEX_HTML = "redirect:/swagger-ui/index.html";

    /**
     * Redirects to the Swagger UI page.
     * <p>
     * When accessing the root URL ('/'), this method redirects the user
     * to the Swagger UI page where the API documentation is visualized.
     * </p>
     *
     * @return A ModelAndView object that redirects to the Swagger UI index page.
     */
    @GetMapping(value = "/")
    public ModelAndView redirectToDocPage() {
        return new ModelAndView(REDIRECT_SWAGGER_UI_INDEX_HTML);
    }

    /**
     * Redirects to the API documentation page.
     * <p>
     * Accessing the '/api-docs' URL will redirect the user to the same
     * Swagger UI page for API documentation visualization.
     * </p>
     *
     * @return A ModelAndView object that redirects to the Swagger UI index page.
     */
    @GetMapping(value = "/api-docs")
    public ModelAndView redirectToApiPage() {
        return new ModelAndView(REDIRECT_SWAGGER_UI_INDEX_HTML);
    }
}
