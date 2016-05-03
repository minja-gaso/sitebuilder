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
import org.sw.marketing.data.website.Data.Website.Template;

@WebServlet("/preview/*")
public class PreviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String paramTemplateId = request.getPathInfo().substring(1);
		long templateID = 0;
		if(paramTemplateId != null)
		{
			try
			{
				templateID = Long.parseLong(paramTemplateId);
			}
			catch(NumberFormatException e)
			{
				templateID = 0;
			}
		}
		
		WebsiteDAO websiteDAO = DAOFactory.getWebsiteDAO();
		Template template = websiteDAO.getWebsiteTemplate(templateID);
		
		long fkSiteId = template.getFkSiteId();
		Website site = websiteDAO.getWebsite(fkSiteId);
		
		String html = template.getHtml();
		String css = site.getCss();
		html = html.replace("{CSS}", "<style type='text/css'>" + css + "</style>");
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().println(html);
	}
}
