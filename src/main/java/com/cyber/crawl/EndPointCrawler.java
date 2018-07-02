package com.cyber.crawl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;	

public class EndPointCrawler {

	private static final Logger logger = LogManager.getLogger(EndPointCrawler.class);


	static HashSet<String> visitedLinks = new HashSet<String>();
	static final int MAX_DEPTH = 30; //Maximum depth for crawling, to avoid link loop... eg. login link on login page
	static int count = 0;
	ObjectMapper mapper = new ObjectMapper();
	static int FAILED = 0;

	//String jsonUrl = "https://raw.githubusercontent.com/OnAssignment/compass-interview/master/data.json";

	public EndPointCrawler() {
		// TODO Auto-generated constructor stub
	}

	/*public EndPointCrawler(String url) {
		jsonUrl = url;
	}*/
	
	
	/* Main method to start the process, expecting an URL through argument, expecting a json url/plain url to process*/
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		if(args.length == 0){
			logger.info("Usage :java -jar {jarfile}.jar {url}");
			System.out.println("Created logfile 'crawl.out' in current directory...!");
			System.exit(0);
		}

		//jsonUrl = args[0];

		crawl(args[0]);
		System.out.println();
		System.out.println("Created logfile 'crawl.out' in current directory...!");
	}

	static void initialize()
	{
		visitedLinks.clear();
		count=0;
		FAILED = 0;
	}

	static public void crawl(String startPointurl) throws JsonParseException, JsonMappingException, IOException
	{
		initialize();

		EndPointCrawler crawl = new EndPointCrawler();

		int count =0;
		System.out.print("\nCrawling :");
		if(startPointurl.endsWith(".json") || startPointurl.endsWith(".JSON"))
		{
			List<String> urls = crawl.parseLinks(startPointurl);
			if(urls !=null )
				for(String url : urls)
				{
					count++;
					crawl.crawlLinks(url, 0);
				}
		}

		if(count == 0)
			crawl.crawlLinks(startPointurl, 0);

		logger.info("");
		printStats();
		logger.info("END");
	}

	public void crawlLinks(String pageUrl, int depth)
	{
		//logger.info("DEPTH : "+depth);
		if(depth == MAX_DEPTH)
			return;
		try{
			if(visitedLinks.contains(pageUrl)) //to avoid circular links
				return;

			System.out.print("|");

			visitedLinks.add(pageUrl);

			if(pageUrl == null || pageUrl.length() == 0)
				throw new IOException();

			logger.info(pageUrl);
			Document document = Jsoup.connect(pageUrl).get();
			Elements pageLinks = document.select("a[href]");

			depth++;
			for (Element link : pageLinks) {
				crawlLinks(link.attr("abs:href"), depth);
			}

		}catch (HttpStatusException hse) {
			logger.info(hse.getMessage());
			FAILED++;
			if(hse.getStatusCode() == 404)
			{}
		}catch(IOException ioe){
			logger.info(ioe.getMessage());
			FAILED++;
		}
	}

	public List<String> parseLinks(String startPointurl) throws JsonParseException, JsonMappingException, IOException
	{
		Links l = null;
		try{
			Document document = Jsoup.connect(startPointurl).get();
			l = mapper.readValue(document.body().text().toString(), Links.class);
		}catch (HttpStatusException hse) {
			if(hse.getStatusCode() == 404)
			{}
		}
		return l!=null ? l.getLinks() : null;
	}

	static void printStats()
	{
		logger.info("Total number of http requests = "+ visitedLinks.size());
		logger.info("Total number of successful requests = "+ (visitedLinks.size() - FAILED));
		logger.info("Total number of failed requests = "+ FAILED);

	}
}
