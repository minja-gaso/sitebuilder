package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sw.marketing.dao.sitebuilder.DAOFactory;
import org.sw.marketing.dao.sitebuilder.WebsiteDAO;
import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Page;
import org.sw.marketing.data.website.Data.Website.Template;

@WebServlet("/page/*")
public class PageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
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
		Page page = websiteDAO.getWebsitePage(pageID);
		
		String html = page.getHtml();		
		
		long fkTemplateId = page.getFkTemplateId();
		Template template = websiteDAO.getWebsiteTemplate(fkTemplateId);
		String templateHtml = template.getHtml();
		
		long fkSiteId = template.getFkSiteId();
		Website site = websiteDAO.getWebsite(fkSiteId);
		String siteCss = site.getCss();

		templateHtml = templateHtml.replace("{CONTENT}", html);
		templateHtml = templateHtml.replace("{CSS}", "<style type='text/css'>" + siteCss + "</style>");
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().println(templateHtml);
	}

}
