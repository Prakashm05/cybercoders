package com.cyber.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RunWith(Parameterized.class)
public class EndPointCrawlerTest{

	EndPointCrawler crawler ;
	String url;
	//protected static final String[] URLS = {"http://url1.com", "http://url2.com", "http://url3.com"};
	//protected static final String[] URLS = {"http://facebook.com", "http://wikipedia.com", "http://url3.com"};
	protected static final String[] URLS = {"https://raw.githubusercontent.com/OnAssignment/compass-interview/master/data.json", 
			"http://dummyurl.com", "http://dummyurl1.com", "http://dummylink1.com", "http://dummyurl.com/dummy.json"};

	@Before
	public void initialize() {
		crawler = new EndPointCrawler();
	}
	public EndPointCrawlerTest( String url) {
		this.url = url;
	}

	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<Object[]>();
		for (String i : URLS) {
			data.add(new String[] {i});
		}
		return data;
	}

	@Test
	public void test() throws JsonParseException, JsonMappingException, IOException {
		crawler.crawl(url);
	}



}
