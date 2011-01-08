/**
 * 
 */
package org.anarxiv;

import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author lihe
 *
 */
public class ArxivLoader 
{
	/**
	 * description of an arxiv paper.
	 */
	public class Paper
	{
		public String _id;
		public String _date;
		public String _title;
		public String _summary;
		public String _url;
		public ArrayList<String> _authors;
		public ArrayList<String> _category;
	}
	
	/**
	 * loader exception.
	 */
	public class LoaderException extends Exception
	{
		public static final long serialVersionUID = 1L;
		
		LoaderException()
		{
			super();
		}
		
		LoaderException(String msg)
		{
			super(msg);
		}
		
		LoaderException(String msg, Throwable cause)
		{
			super(msg, cause);
		}
	}
	
	/**
	 * current query start point.
	 */
	private int _qStart = 0;
	
	/**
	 * the query url.
	 */
	private String _qUrl = null;
	
	/**
	 * the query category.
	 */
	private String _qCat = null;
	
	/**
	 * max results.
	 */
	private int _maxResults = 10;
	
	/**
	 * load paper list from specified url.
	 */
	public ArrayList<Paper> loadPapers(String category) throws LoaderException
	{
		/* invalid query string. */
		if(category == null || category.equals(""))
			throw new LoaderException("Invalid category name.");
		
		/* check if query changed. */
		/* call equals from category since _qCat may be null. */
		if(category.equals(_qCat) == false)
		{
			_qCat = category;
			_qStart = 0;
		}
		
		/* get url. */
		_qUrl = UrlTable.makeQueryUrl(_qCat, _qStart, _maxResults);
		
		/* query the url using URL. */
		Document doc = null;
		
		try
		{
			URL url = new URL(_qUrl);
			URLConnection conn = url.openConnection();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(conn.getInputStream());
		}
		catch(MalformedURLException e)
		{
			throw new LoaderException(e.getMessage(), e);
		}
		catch(IOException e)
		{
			throw new LoaderException(e.getMessage(), e);
		}
		catch(Exception e)
		{
			throw new LoaderException(e.getMessage());
		}
		
		NodeList entryList = doc.getElementsByTagName("entry");
		
		/* allocate paper list. */
		ArrayList<Paper> paperList = new ArrayList<Paper>();
		
		/* extract paper info. */
		for(int i = 0; i < entryList.getLength(); i ++)
		{
			Element node = (Element)entryList.item(i);
			
			/* get simple tags. */
			String id = node.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
			String title = node.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
			String summary = node.getElementsByTagName("summary").item(0).getFirstChild().getNodeValue();
			String date = node.getElementsByTagName("published").item(0).getFirstChild().getNodeValue();
			
			/* get author list. */
			ArrayList<String> authors = new ArrayList<String>();
			NodeList authorList = node.getElementsByTagName("author");
			for(int j = 0; j < authorList.getLength(); j ++)
			{
				Element authorNode = (Element)authorList.item(j);
				String ath = authorNode.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
				authors.add(ath);
			}
			
			/* get url. */
			String url = ((Element)node.getElementsByTagName("link").item(1)).getAttribute("href");
			
			/* fill in paper structure. */
			Paper entry = new Paper();
			entry._id = id;
			entry._date = date;
			entry._title = title;
			entry._summary = summary;
			entry._authors = authors;
			entry._url = url;
			
			paperList.add(entry);
		}
		
		/* increase starting point. */
		_qStart += _maxResults;
		
		return paperList;
	}
}
