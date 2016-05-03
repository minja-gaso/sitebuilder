package servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Page;
import org.sw.marketing.data.website.Data.Website.Template;


public class PageParameters
{
	public static Page process(HttpServletRequest request, Page page)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("PAGE_TITLE") != null)
		{
			page.setTitle(parameterMap.get("PAGE_TITLE")[0]);
		}
		if(parameterMap.get("PAGE_HTML") != null)
		{
			page.setHtml(parameterMap.get("PAGE_HTML")[0]);
		}
		if(parameterMap.get("PAGE_TEMPLATE") != null)
		{
			long templateID = 0;
			try
			{
				templateID = Long.parseLong(parameterMap.get("PAGE_TEMPLATE")[0]);
			}
			catch(NumberFormatException e)
			{
				templateID = 0;
			}
			page.setFkTemplateId(templateID);
		}
		
		return page;
	}
}
