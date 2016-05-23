package servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.sitebuilder.DAOFactory;
import org.sw.marketing.dao.sitebuilder.WebsiteDAO;
import org.sw.marketing.data.website.Data;
import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Page;
import org.sw.marketing.data.website.Data.Website.Template;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.data.website.Data.Website.Page.Component;
import org.sw.marketing.data.website.Data.Website.Page.Component.Item;
import org.sw.marketing.util.ReadFile;

@WebServlet("/page/*")
public class PageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		String paramPageId = request.getPathInfo().substring(1);
		long pageID = 0;
		if(paramPageId != null)
		{
			try
			{
				pageID = Long.parseLong(paramPageId);
			}
			catch(NumberFormatException e)
			{
				pageID = 0;
			}
		}

		WebsiteDAO websiteDAO = DAOFactory.getWebsiteDAO();		
		Data data = new Data();
		Page page = websiteDAO.getWebsitePage(pageID);
		if(page != null)
		{
			long fkSiteId = page.getFkSiteId();
			Website website = websiteDAO.getWebsite(fkSiteId);

			java.util.List<Component> components = websiteDAO.getComponents(pageID);
			if(components != null)
			{
				for(Component component : components)
				{
					if(component != null)
					{
						java.util.List<Item> items = websiteDAO.getComponentItems(component.getId());
						if(items != null)
						{
							component.getItem().addAll(items);
						}
						page.getComponent().add(component);
					}
				}
			}
			
			if(page != null)
			{
				website.getPage().add(page);
				data.getWebsite().add(website);
			}
			
			long fkTemplateId = page.getFkTemplateId();
			Template template = websiteDAO.getWebsiteTemplate(fkTemplateId, fkSiteId);
			if(template != null)
			{
				String templateHtml = template.getHtml();
				
				String contentStr = null;
				if(page.getHtml().length() > 0)
				{
					contentStr = page.getHtml();
				}
				else
				{
					TransformerHelper transformerHelper = new TransformerHelper();
					String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.website", data);
					String xslScreen = getServletContext().getInitParameter("xslPath") + "public\\element.xsl";
					String xslStr = ReadFile.getSkin(xslScreen);
					contentStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
					
					System.out.println(xmlStr);
				}
				templateHtml = templateHtml.replace("{CONTENT}", contentStr);
				templateHtml = templateHtml.replace("{TITLE}", page.getTitle());
				templateHtml = templateHtml.replace("{SUBTITLE}", page.getSubtitle());
				templateHtml = templateHtml.replace("{FOOTER}", website.getFooter());
				templateHtml = templateHtml.replace("{CSS}", "<style type='text/css'>" + website.getCss() + "</style>");
				response.getWriter().println(templateHtml);
			}
			else
			{
				response.getWriter().println(page.getHtml());
			}
		}
		
//		String paramWebsiteId = request.getParameter("WEBSITE_ID");
//		long websiteID = 0;
//		if(paramWebsiteId != null)
//		{
//			try
//			{
//				websiteID = Long.parseLong(paramWebsiteId);
//			}
//			catch(NumberFormatException e)
//			{
//				websiteID = 0;
//			}
//		}
		
		
	}

}
