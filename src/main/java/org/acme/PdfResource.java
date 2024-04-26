package org.acme;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.PartType;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.PdfOptions;
import com.microsoft.playwright.options.Media;
import com.microsoft.playwright.Playwright;


@Path("/pdf")
public class PdfResource {

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response renderPdf(@QueryParam("orientation") String orientation) {
        // Implement PDF rendering logic using Playwright
        byte[] pdfContent = createPdfWithPlaywrightFromUrl(orientation);
        return Response.ok(pdfContent, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"output.pdf\"")
                .build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response renderPdf(@MultipartForm FileUploadForm form) {
        // Extract the HTML content from the uploaded file
        String htmlContent = new String(form.getFileData());

        // Call the method to render this HTML to PDF
        byte[] pdfContent = createPdfWithPlaywright(htmlContent);

        // Return the PDF file
        return Response.ok(pdfContent, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"output.pdf\"")
                .build();
    }

    private byte[] createPdfWithPlaywright(String htmlContent) {
      try (Playwright playwright = Playwright.create()) {
        Browser browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        // Set the HTML content
        page.setContent(htmlContent);

        // Generate PDF
        page.emulateMedia(new Page.EmulateMediaOptions().setMedia(Media.PRINT));
        byte[] pdfContent = page.pdf(new Page.PdfOptions());
        return pdfContent;
      }
    }

    // Inner class for file upload
    public static class FileUploadForm {

        @FormParam("file")
        @PartType(MediaType.TEXT_PLAIN)
        private byte[] fileData;

        public byte[] getFileData() {
            return fileData;
        }

        public void setFileData(byte[] fileData) {
            this.fileData = fileData;
        }
    }

    private byte[] createPdfWithPlaywrightFromUrl(String orientation) {
      try (Playwright playwright = Playwright.create()) {
        Browser browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        // Navigate to your HTML content
        page.navigate("https://playwright.dev/java/docs/intro");

        // Set page orientation
        // PdfOptions options = new PdfOptions();
        // if ("right".equals(orientation)) {
        //     options.setLandscape(true);
        // } else if ("left".equals(orientation)) {
        //     // Logic for left orientation
        // }
        byte[] pdfContent = page.pdf();
        return pdfContent;
      }
    }
}
