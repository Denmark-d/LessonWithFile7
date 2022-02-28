package ru.Denmark;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import static com.codeborne.selenide.Selectors.byText;

public class FileParsingTest {

    private ClassLoader cl = FileParsingTest.class.getClassLoader();

    @Test
    void parsePdfTest() throws Exception {
        Selenide.open("https://junit.org/junit5/docs/current/user-guide/");
        File pdfDownload = Selenide.$(byText("PDF download")).download();
        PDF parsed = new PDF(pdfDownload);
        assertThat(parsed.author).contains("Marc Philipp");
    }

    @Test
    void parseXlsTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("files/0303.xlsx")) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue())
                    .isEqualTo("Help");
        }
    }

    @Test
    void parseCsvFile() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("files/11.csv")) {
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();
            assertThat(list)
                    .hasSize(1)
                    .contains(
                            new String[]{"Author", "Book"}
                            //new String[] {"Block", "Apteka"},
                            //new String[] {"Esenin", "Cherniy Chelovek"}
                    );
        }
    }

    @Test
    void zipTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("files/upload.txt.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {
            ZipEntry zipEntry; //упакованные в zip архив папки, объявили переменную
            while ((zipEntry = zis.getNextEntry()) != null) { //пока в стриме есть zip мы можем её получать
                assertThat(zipEntry.getName()).isEqualTo("upload.txt");
            }
        }
    }

    @Test
    void zipTest1() throws Exception {
        ZipFile zipFile = new ZipFile("/Users/daniiaadiiakova/IdeaProjects/LessonWithFile7/src/test/resources/files/test.zip");
        // Проверка csv
            ZipEntry csvEntry = zipFile.getEntry("11.csv");
            try (InputStream stream = cl.getResourceAsStream("files/11.csv")) {
                CSVReader reader = new CSVReader(new InputStreamReader(stream));
                List<String[]> list = reader.readAll();
                assertThat(list)
                        .hasSize(1)
                        .contains(
                                new String[] {"Author", "Book"}
                                //new String[] {"Block", "Apteka"},
                                //new String[] {"Esenin", "Cherniy Chelovek"}
                        );
            }


        //ZipFile zf = new ZipFile(new File(cl.getResource("files/sample-zip-file.zip").toURI()));


    }

    @Test
    void zipTest2() throws Exception {
        ZipFile zipFile = new ZipFile("/Users/daniiaadiiakova/IdeaProjects/LessonWithFile7/src/test/resources/files/Архив.zip");

        //Проверка pdf
        ZipEntry pdfEntry = zipFile.getEntry("Setter.pdf");
        try (InputStream stream = zipFile.getInputStream(pdfEntry)) {
        PDF parsed = new PDF(stream);
        assertThat(parsed.text).contains("Сеттер");
         }

        //Проверка xls
        ZipEntry XlsEntry = zipFile.getEntry("0303.xlsx");
        try (InputStream stream = zipFile.getInputStream(XlsEntry)) {
            XLS parsed = new XLS(stream);
            assertThat(parsed.excel.getSheetAt(0).getRow(1).getCell(0).getStringCellValue()).isEqualTo("Help");
        }

        // Проверка csv
        ZipEntry csvEntry = zipFile.getEntry("11.csv");
        try (InputStream stream = cl.getResourceAsStream("files/11.csv")) {
            CSVReader reader = new CSVReader(new InputStreamReader(stream));
            List<String[]> list = reader.readAll();
            assertThat(list)
                    .hasSize(1)
                    .contains(
                            new String[] {"Author", "Book"}
                            //new String[] {"Block", "Apteka"},
                            //new String[] {"Esenin", "Cherniy Chelovek"}
                    );
        }


    }
}

