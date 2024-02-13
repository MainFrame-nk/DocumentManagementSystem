import org.junit.Test;
import ru.example.Attributes;
import ru.example.Document;
import ru.example.DocumentManagementSystem;
import ru.example.UnknownFileTypeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static ru.example.Attributes.*;

// tag::eg_constants
public class DocumentManagementSystemTest {
    private static final String RESOURCES =
            "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    private static final String LETTER = RESOURCES + "patient.letter";
    private static final String REPORT = RESOURCES + "patient.report";
    private static final String XRAY = RESOURCES + "xray.jpg";
    private static final String INVOICE = RESOURCES + "patient.invoice";
    private static final String JOHN_COLBY = "John Colby";

    // end::eg_constants[]

    private DocumentManagementSystem system = new DocumentManagementSystem();

    // tag::shouldImportFile[]
    @Test
    public void shouldImportFile() throws Exception {
        system.importFile(LETTER);

        final Document document = onlyDocument();

        assertAttributeEquals(document, Attributes.PATH, LETTER);
    }
    // end::shouldImportFile[]

    // tag::shouldImportLetterAttributes[]
    @Test
    public void shouldImportLetterAttributes() throws Exception {
        system.importFile(LETTER);

        final Document document = onlyDocument();

        assertAttributeEquals(document, PATIENT, JOHN_COLBY);
        assertAttributeEquals(document, ADDRESS,
                "123 Fake Street\n" +
                    "Westminster\n" +
                    "London\n" +
                    "United Kingdom");
        assertAttributeEquals(document, BODY,
                "We are writing to you to confirm the re-scheduling of your appointment\n" +
                "with Dr. Jovah from 29th December 2023 to 5th January 2024.");
        assertTypeIs("LETTER", document);
    }
    // end::shouldImportLetterAttributes[]

    @Test
    public void shouldImportReportAttributes() throws Exception {
        system.importFile(REPORT);

        assertIsReport(onlyDocument());
    }

    // tag::shouldImportImageAttributes[]
    @Test
    public void shouldImportImageAttributes() throws Exception {
        system.importFile(XRAY);

        final Document document = onlyDocument();

        assertAttributeEquals(document, WIDTH, "320");
        assertAttributeEquals(document, HEIGHT, "179");
        assertTypeIs("IMAGE", document);
    }
    // end::shouldImportImageAttributes[]

    @Test
    public void shouldImportInvoiceAttributes() throws Exception
    {
        system.importFile(INVOICE);

        final Document document = onlyDocument();

        assertAttributeEquals(document, PATIENT, JOHN_COLBY);
        assertAttributeEquals(document, AMOUNT, "$100");
        assertTypeIs("INVOICE", document);
    }

    @Test
    public void shouldBeAbleToSearchFilesByAttributes() throws Exception {
        system.importFile(LETTER);
        system.importFile(REPORT);
        system.importFile(XRAY);

        final List<Document> documents = system.search("patient:John,body:Diet Coke");
        assertThat(documents, hasSize(1));

        assertIsReport(documents.get(0));
    }

    // tag::errorTests[]
    @Test(expected = FileNotFoundException.class)
    public void shouldNotImportMissingFile() throws Exception {
        system.importFile("gobbledygook.txt");
    }

    @Test(expected = UnknownFileTypeException.class)
    public void shouldNotImportUnknownFile() throws Exception {
        system.importFile(RESOURCES + "unknown.txt");
    }
    // end::errorTests[]

    private void assertIsReport(final Document document)
    {
        assertAttributeEquals(document, PATIENT, JOHN_COLBY);
        assertAttributeEquals(document, BODY,
                "On 5th January 2024 I examined John's teeth.\n" +
                        "We discussed his switch from drinking Coke to Diet Coke.\n" +
                        "No new problems were noted with his teeth.");
        assertTypeIs("REPORT", document);
    }

    // tag::assertAttributeEquals[]
    private void assertAttributeEquals(
            final Document document,
            final String attributeName,
            final String expectedValue)
    {
        assertEquals(
                "Document has the wrong value for " + attributeName,
                expectedValue,
                document.getAttribute(attributeName));
    }
    // end::assertAttributeEquals[]

    private void assertTypeIs(final String type, final Document document)
    {
        assertAttributeEquals(document, TYPE, type);
    }

    // tag::onlyDocument[]
    private Document onlyDocument()
    {
        final List<Document> documents = system.contents();
        assertThat(documents, hasSize(1));
        return documents.get(0);
    }
    // end::onlyDocument[]
}
