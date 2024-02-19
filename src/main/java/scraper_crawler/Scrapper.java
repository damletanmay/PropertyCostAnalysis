package scraper_crawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scrapper {
	public static void main(String[] args) {
		try {
			Document document= Jsoup.connect("https://books.toscrape.com/").get();
			Elements book_names = document.select("li.col-xs-6 > article > h3 > a");
			Elements prices = document.select("li.col-xs-6 > article > div.product_price > p.price_color");
			HashMap<String,Float> books_data = new HashMap <>();
			
			Iterator<Element> book = book_names.iterator();
			Iterator<Element> price = prices.iterator();
					
			while (book.hasNext() && price.hasNext()) {
				books_data.put(book.next().text(),Float.valueOf(price.next().text().substring(1)));
			}
			
			System.out.println(books_data);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
