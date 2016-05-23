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
import org.sw.marketing.data.website.Data.Website.ArchivePage;
import org.sw.marketing.data.website.Data.Website.Page;
import org.sw.marketing.data.website.Data.Website.Page.Component;
import org.sw.marketing.data.website.Data.Website.Page.Component.Item;
import org.sw.marketing.data.website.Data.Website.Template;
import org.sw.marketing.data.website.Environment;
import org.sw.marketing.transformation.TransformerHelper;
import org.sw.marketing.util.ReadFile;

import servlet.params.ComponentItemParameters;
import servlet.params.ComponentParameters;
import servlet.params.PageParameters;
import servlet.params.SiteParameters;
import servlet.params.TemplateParameters;

@WebServlet("/controller")
public class FrontController extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		boolean isAdmin = false;
		if(request.getParameter("isAdmin") != null)
		{
			String isAdminStr = request.getParameter("isAdmin");
			if(isAdminStr.equals("true"))
			{
				isAdmin = true;
			}
		}
		
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
		
		String paramArchiveId = request.getParameter("ARCHIVE_ID");
		long archiveID = 0;
		if(paramArchiveId != null)
		{
			try
			{
				archiveID = Long.parseLong(paramArchiveId);
			}
			catch(NumberFormatException e)
			{
				archiveID = 0;
			}
		}
		
		String paramComponentId = request.getParameter("COMPONENT_ID");
		long componentID = 0;
		if(paramComponentId != null)
		{
			try
			{
				componentID = Long.parseLong(paramComponentId);
			}
			catch(NumberFormatException e)
			{
				componentID = 0;
			}
		}
		
		String paramComponentItemId = request.getParameter("COMPONENT_ITEM_ID");
		long componentItemID = 0;
		if(paramComponentItemId != null)
		{
			try
			{
				componentItemID = Long.parseLong(paramComponentItemId);
			}
			catch(NumberFormatException e)
			{
				componentItemID = 0;
			}
		}
		
		Data data = new Data();
		data.setAdmin(isAdmin);
		Website website = websiteDAO.getWebsite(websiteID);
		java.util.List<Website> websites = null;
		
		Template template = websiteDAO.getWebsiteTemplate(templateID, websiteID);
		java.util.List<Template> templates = null;
		Page page = websiteDAO.getWebsitePage(pageID, websiteID);
		java.util.List<Page> pages = null;
		Component component = websiteDAO.getComponent(componentID);
		java.util.List<Component> components = websiteDAO.getComponents(pageID);
		Item item = websiteDAO.getComponentItem(componentItemID);
		java.util.List<Item> items = websiteDAO.getComponentItems(componentID);
		
		boolean isNewWebsite = false;
		
		if(parameterMap.get("ACTION") != null)
		{
			String paramAction = parameterMap.get("ACTION")[0];
			if(paramAction.equals("CREATE_WEBSITE"))
			{
				websiteID = websiteDAO.createWebsite();
				website = websiteDAO.getWebsite(websiteID);
				isNewWebsite = true;
			}
			if(paramAction.equals("DELETE_WEBSITE"))
			{
				websiteDAO.deleteWebsite(websiteID);
				websiteID = 0;
				website = null;
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
				templateID = websiteDAO.createWebsiteTemplate(websiteID);
				template = websiteDAO.getWebsiteTemplate(templateID, websiteID);
			}
			else if(paramAction.equals("DELETE_TEMPLATE"))
			{
				websiteDAO.deleteWebsiteTemplate(templateID);
			}
			else if(paramAction.equals("SAVE_PAGE"))
			{
				page = PageParameters.process(request, page);
				websiteDAO.updateWebsitePage(page);
				websiteDAO.archiveWebsitePage(page);
			}
			else if(paramAction.equals("CREATE_PAGE"))
			{
				pageID = websiteDAO.createWebsitePage(websiteID);
				page = websiteDAO.getWebsitePage(pageID, websiteID);
			}
			else if(paramAction.equals("DELETE_PAGE"))
			{
				websiteDAO.deleteWebsitePage(pageID);
			}
			else if(paramAction.equals("APPLY_ARCHIVE"))
			{
				Page archivePage = websiteDAO.getWebsitePageArchive(archiveID);
				archivePage = PageParameters.process(request, page);
				archivePage.setId(pageID);
				websiteDAO.applyArchivePage(archivePage);
			}
			else if(paramAction.equals("CREATE_COMPONENT"))
			{
				if((parameterMap.get("COMPONENT_TYPE") != null))
				{
					String paramComponentType = parameterMap.get("COMPONENT_TYPE")[0];
					componentID = websiteDAO.createComponent(paramComponentType, pageID);
					component = websiteDAO.getComponent(componentID);
					if(components != null)
					{
						int orderNumber = components.size() + 1;
						component.setOrderNumber(orderNumber);
						websiteDAO.updateComponent(component);
					}
					components = websiteDAO.getComponents(pageID);
				}
			}
			else if(paramAction.equals("CREATE_COMPONENT_ITEM"))
			{					
				long itemID = websiteDAO.createComponentItem(componentID);
				item = websiteDAO.getComponentItem(itemID);
				if(items != null)
				{
					int orderNumber = items.size() + 1;
					item.setOrderNumber(orderNumber);
					websiteDAO.updateComponentItem(item);
				}				
				items = websiteDAO.getComponentItems(componentID);
			}
			else if(paramAction.equals("SAVE_COMPONENT"))
			{
				component = ComponentParameters.process(request, component);
				websiteDAO.updateComponent(component);
			}
			else if(paramAction.equals("DELETE_COMPONENT"))
			{
				websiteDAO.deleteComponent(componentID);
				components = websiteDAO.getComponents(pageID);
			}	
			else if(paramAction.equals("MOVE_COMPONENT"))
			{
				String paramComponentOrderNumber = request.getParameter("COMPONENT_ORDER_NUMBER");
				int orderNumber = Integer.parseInt(paramComponentOrderNumber);
				
				Component first = websiteDAO.getComponent(componentID);
				Component second = websiteDAO.getComponentByOrderNumber(orderNumber);
				int originalOrderNumber = first.getOrderNumber();
				
				first.setOrderNumber(orderNumber);				
				second.setOrderNumber(originalOrderNumber);
				websiteDAO.updateComponent(first);
				websiteDAO.updateComponent(second);
				
				components = websiteDAO.getComponents(pageID);
			}	
			else if(paramAction.equals("SAVE_COMPONENT_ITEM"))
			{
				item = ComponentItemParameters.process(request, item);
				websiteDAO.updateComponentItem(item);
			}
			else if(paramAction.equals("DELETE_COMPONENT_ITEM"))
			{
				websiteDAO.deleteComponentItem(componentItemID);
				items = websiteDAO.getComponentItems(componentID);
			}	
			else if(paramAction.equals("MOVE_COMPONENT_ITEM"))
			{
				String paramComponentItemOrderNumber = request.getParameter("COMPONENT_ITEM_ORDER_NUMBER");
				int orderNumber = Integer.parseInt(paramComponentItemOrderNumber);
				
				Item first = websiteDAO.getComponentItem(componentItemID);
				Item second = websiteDAO.getComponentItemByOrderNumber(orderNumber);
				int originalOrderNumber = first.getOrderNumber();
				
				first.setOrderNumber(orderNumber);				
				second.setOrderNumber(originalOrderNumber);
				websiteDAO.updateComponentItem(first);
				websiteDAO.updateComponentItem(second);
				
				items = websiteDAO.getComponentItems(componentID);
			}			
		}
		
		if(websiteID == 0)
		{
			websites = websiteDAO.getWebsites();
		}
		
		templates = websiteDAO.getWebsiteTemplates(websiteID);
		pages = websiteDAO.getWebsitePages(websiteID);		
		
		String xslScreen = null;
		if((parameterMap.get("SCREEN") != null))
		{
			String paramScreen = parameterMap.get("SCREEN")[0];
			
			if(paramScreen.equals("GENERAL") || isNewWebsite)
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
				
				if(templates != null)
				{
					website.getTemplate().addAll(templates);
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
				
				if(components != null)
				{
					page.getComponent().addAll(components);
				}
			}
			else if(paramScreen.equals("PAGE_ARCHIVE"))
			{
				xslScreen = "page_archive.xsl";
//				website.getPage().add(page);
				Page archivePage = null;
				if(archiveID == 0)
				{
					archivePage = websiteDAO.getWebsitePage(pageID, websiteID);
				}
				else
				{
					archivePage = websiteDAO.getWebsitePageArchive(archiveID);
				}
				if(archivePage != null)
				{
					website.getPage().add(archivePage);
				}
				
				java.util.List<ArchivePage> archivePages = websiteDAO.getWebsitePageArchives(page.getId());
				if(archivePages != null)
				{
					website.getArchivePage().addAll(archivePages);
				}
			}
			else if(paramScreen.equals("FOOTER"))
			{
				xslScreen = "footer.xsl";
			}
			else if(paramScreen.equals("CSS"))
			{
				xslScreen = "css.xsl";
			}
			else if(paramScreen.equals("COMPONENT"))
			{
				xslScreen = "component.xsl";
				
//				String paramComponentId = parameterMap.get("COMPONENT_ID")[0];
//				long componentID = Long.parseLong(paramComponentId);
				
				if(component != null)
				{
					if(items != null)
					{
						component.getItem().addAll(items);
					}
					page.getComponent().add(component);				
				}
				website.getPage().add(page);
			}
			else if(paramScreen.equals("COMPONENT_ITEM"))
			{
				xslScreen = "component_item.xsl";
				
				if(component != null)
				{
					if(item != null)
					{
						component.getItem().add(item);
					}
					page.getComponent().add(component);
				}
				website.getPage().add(page);
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
			if(websites != null)
			{
				data.getWebsite().addAll(websites);
			}
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
