import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Soma {

	private static WebDriver driver;
	
	private static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	private static final String WEB_DRIVER_PATH = "exe/chromedriver";
	
	private static String[] categories = {
			"iot",
			"IOT",
			"사물",
			"임베디드",
			"embeded"
	};
	
	private static String[] somaPeoples = new String[13];
	private static String[] mainNotices = new String[7];
	private static String[] somaTech = new String[1];
	private static String[] swInfo = new String[14];
	
	static {
		for(int i=0;i<somaPeoples.length;i++) {
			somaPeoples[i] = "https://blog.naver.com/PostList.nhn?from=postList&blogId=sw_maestro&categoryNo=21&parentCategoryNo=21&currentPage=" + (i+1);
		}
		
		for(int i=0;i<mainNotices.length;i++) {
			mainNotices[i] = "https://blog.naver.com/PostList.nhn?from=postList&blogId=sw_maestro&categoryNo=11&currentPage=" + (i+1);
		}
		
		//for(int i=0;i<somaTech.length;i++)
		somaTech[0] = "https://blog.naver.com/PostList.nhn?blogId=sw_maestro&from=postList&categoryNo=22&parentCategoryNo=22";
		
		for(int i=0;i<swInfo.length;i++) {
			swInfo[i] = "https://blog.naver.com/PostList.nhn?from=postList&blogId=sw_maestro&categoryNo=13&parentCategoryNo=13&currentPage=" + (i+1);
		}
	}
	
	static LinkedHashSet<String> results = new LinkedHashSet<>();
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sb = new StringBuilder();
		int input = 1;
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		driver = new ChromeDriver(options);
		
		System.out.print("1.소마 크롤링 \n2.특정 페이지 검사: ");
		
		input = Integer.parseInt(br.readLine());
		
		if(input ==1) {
			
			System.out.println("-------------------------- Soma Peoples --------------------------");
			getResults(somaPeoples);
			
			System.out.println("-------------------------- Main Notices --------------------------");
			getResults(mainNotices);
			
			System.out.println("-------------------------- Soma Tech --------------------------");
			getResults(somaTech);
			
			System.out.println("-------------------------- SW Info --------------------------");
			getResults(swInfo);
			
			for(String result: results) {
				sb.append(result+"\n");
			}
			
			System.out.println(results.size() +" 개의 결과");
			System.out.println(sb);
		} else {
			System.out.println("url 입력: ");
			String url = br.readLine();
			testPage(url);
		}
		br.close();
	}
	
	private static void getResults(String[] pages) {
		LinkedList<String> links = new LinkedList<>();
		int resultNum = 0;
		for(String sp: pages) {
			driver.get(sp);
			
			Document doc = Jsoup.parse(driver.getPageSource());
			
			Elements pcol2 = doc.getElementsByClass("link pcol2");
			
			for(Element e: pcol2) {
				links.add("https://blog.naver.com" +e.attr("href"));
			}
		}
		
		for(String link: links) {
			driver.get(link);
			
			Document doc = Jsoup.parse(driver.getPageSource());
			
			int sum = getSumOfResults(doc);
			
			if(sum !=0) {
				results.add(link);
				resultNum++;
			}
		}
		
		results.add("-------------------------- "+resultNum +" ---------------");
		System.out.println("results: " + resultNum);
	}
	
	private static int getSumOfResults(Document doc) {
		int sum = 0;
		
		for(String title: categories) {
			Elements ele = doc.getElementsContainingOwnText(title);
			
			//밑에 리스트에서 나오는 아이들은 제외
			ele.removeIf(a -> {
				if(a.className().equals("title ell"))
					return true;
				else 
					return false;
			});
			
			sum += ele.size();
		}
		
		return sum;
	}
	
	private static void testPage(String url) {
		driver.get(url);
		
		Document doc = Jsoup.parse(driver.getPageSource());
		
		for(String title: categories) {
			Elements ele = doc.getElementsContainingOwnText(title);
			ele.removeIf(a -> {
				if(a.className().equals("title ell"))
					return true;
				else 
					return false;
			});
			for(Element e: ele) {
				System.out.println(e.text());
				System.out.println(e.className());
			}
		}
	}

}
