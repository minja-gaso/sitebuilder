package servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sw.marketing.dao.sitebuilder.DAOFactory;
import org.sw.marketing.dao.sitebuilder.WebsiteDAO;
import org.sw.marketing.data.website.Data;
import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Page;
import org.sw.marketing.data.website.Data.Website.Template;
import org.sw.marketing.data.website.Environment;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

import servlet.params.PageParameters;
import servlet.params.SiteParameters;
import servlet.params.TemplateParameters;

@WebServlet("/controller")
public class FrontController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession httpSession = request.getSession();
		WebsiteDAO websiteDAO = DAOFactory.getWebsiteDAO();
		
		/*
		 * process all fields
		 */
		java.util.Map<String, String[]> parameterMap = new java.util.HashMap<String, String[]>();
		java.util.Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements())
		{
			String parameterName = (String) parameterNames.nextElement();
			String[] parameterValue = request.getParameterValues(parameterName);
			parameterMap.put(parameterName, parameterValue);
		}
		request.setAttribute("parameterMap", parameterMap);
		
		String paramWebsiteId = request.getParameter("WEBSITE_ID");
		long websiteID = 0;
		if(paramWebsiteId != null)
		{
			try
			{
				websiteID = Long.parseLong(paramWebsiteId);
			}
			catch(NumberFormatException e)
			{
				websiteID = 0;
			}
		}
		
		String paramTemplateId = request.getParameter("TEMPLATE_ID");
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
		
		String paramPageId = request.getParameter("PAGE_ID");
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
		
		Data data = new Data();
		Website website = websiteDAO.getWebsite(websiteID);
		java.util.List<Website> websites = null;
		if(website == null)
		{
			websites = websiteDAO.getWebsites();
		}
		Template template = websiteDAO.getWebsiteTemplate(templateID);
		java.util.List<Template> templates = websiteDAO.getWebsiteTemplates();
		Page page = websiteDAO.getWebsitePage(pageID);
		java.util.List<Page> pages = websiteDAO.getWebsitePages();
		
		if(parameterMap.get("ACTION") != null)
		{
			String paramAction = parameterMap.get("ACTION")[0];
			if(paramAction.equals("CREATE_WEBSITE"))
			{
				websiteID = websiteDAO.createWebsite();
				website = websiteDAO.getWebsite(websiteID);
			}
			else if(paramAction.equals("SAVE_WEBSITE"))
			{
				website = SiteParameters.process(request, website);
				websiteDAO.updateWebsite(website);
			}
			else if(paramAction.equals("SAVE_TEMPLATE"))
			{
				template = TemplateParameters.process(request, template);
				websiteDAO.updateWebsiteTemplate(template);
			}
			else if(paramAction.equals("CREATE_TEMPLATE"))
			{
				templateID = websiteDAO.createWebsiteTemplate();
				template = websiteDAO.getWebsiteTemplate(templateID);
			}
			else if(paramAction.equals("SAVE_PAGE"))
			{
				page = PageParameters.process(request, page);
				websiteDAO.updateWebsitePage(page);
			}
			else if(paramAction.equals("CREATE_PAGE"))
			{
				pageID = websiteDAO.createWebsitePage();
				page = websiteDAO.getWebsitePage(pageID);
			}
		}
		
		
		String xslScreen = null;
		if((parameterMap.get("SCREEN") != null))
		{
			String paramScreen = parameterMap.get("SCREEN")[0];
			
			if(paramScreen.equals("GENERAL"))
			{
				xslScreen = "general.xsl";
			}
			else if(paramScreen.equals("TEMPLATES"))
			{
				xslScreen = "templates.xsl";
				
				if(templates != null)
				{
					website.getTemplate().addAll(templates);
				}
			}
			else if(paramScreen.equals("TEMPLATE"))
			{
				xslScreen = "template.xsl";
				website.getTemplate().add(template);
			}
			else if(paramScreen.equals("PAGES"))
			{
				xslScreen = "pages.xsl";
				
				if(pages != null)
				{
					website.getPage().addAll(pages);
				}
			}
			else if(paramScreen.equals("PAGE"))
			{
				xslScreen = "page.xsl";
				website.getPage().add(page);
				
				if(templates != null)
				{
					website.getTemplate().addAll(templates);
				}
			}
			else if(paramScreen.equals("CSS"))
			{
				xslScreen = "css.xsl";
			}
			else
			{
				xslScreen = "list.xsl";
			}
		}
		else
		{
			xslScreen = "list.xsl";
		}
		
		if(website != null)
		{
			data.getWebsite().add(website);
		}
		else 
		{
			data.getWebsite().addAll(websites);
		}
		
		Environment environment = new Environment();
		environment.setServerName(getBaseUrl(request));
		data.setEnvironment(environment);
		
		TransformerHelper transformerHelper = new TransformerHelper();
		transformerHelper.setUrlResolverBaseUrl(getServletContext().getInitParameter("xslUrl"));
		
		String xmlStr = transformerHelper.getXmlStr("org.sw.marketing.data.website", data);
		xslScreen = getServletContext().getInitParameter("xslPath") + xslScreen;
		String xslStr = ReadFile.getSkin(xslScreen);
		String htmlStr = transformerHelper.getHtmlStr(xmlStr, new ByteArrayInputStream(xslStr.getBytes()));
		
		String toolboxSkinPath = getServletContext().getInitParameter("assetPath") + "sb.html";
		String skinHtmlStr = ReadFile.getSkin(toolboxSkinPath);
		skinHtmlStr = skinHtmlStr.replace("{NAME}", "SiteBuilder");
		skinHtmlStr = skinHtmlStr.replace("{CONTENT}", htmlStr);
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().println(skinHtmlStr);
		System.out.println(xmlStr);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		process(request, response);
	}

	public static String getBaseUrl(HttpServletRequest request) 
	{
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
		{
			return request.getScheme() + "://" + request.getServerName();	
		}
		else
		{

			return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		}
	}
}
