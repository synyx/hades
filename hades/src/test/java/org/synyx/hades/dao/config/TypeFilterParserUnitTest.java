package org.synyx.hades.dao.config;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class TypeFilterParserUnitTest {

    private TypeFilterParser parser;
    private Element documentElement;

    @Mock
    private ClassLoader classLoader;

    @Mock
    private ReaderContext context;

    @Mock
    private ClassPathScanningCandidateComponentProvider scanner;


    @Before
    public void setUp() throws SAXException, IOException,
            ParserConfigurationException {

        parser = new TypeFilterParser(classLoader, context);

        Resource sampleXmlFile =
                new ClassPathResource("config/type-filter-test.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        documentElement =
                factory.newDocumentBuilder().parse(
                        sampleXmlFile.getInputStream()).getDocumentElement();
    }


    @Test
    public void parsesIncludesCorrectly() throws Exception {

        Element element =
                DomUtils.getChildElementByTagName(documentElement,
                        "firstSample");

        parser.parseFilters(element, scanner);

        verify(scanner, atLeastOnce()).addIncludeFilter(
                isA(AssignableTypeFilter.class));
    }


    @Test
    public void parsesExcludesCorrectly() throws Exception {

        Element element =
                DomUtils.getChildElementByTagName(documentElement,
                        "secondSample");

        parser.parseFilters(element, scanner);

        verify(scanner, atLeastOnce()).addExcludeFilter(
                isA(AssignableTypeFilter.class));
    }
}
