package servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.website.Data.Website;


public class SiteParameters
{
	public static Website process(HttpServletRequest request, Website website)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("WEBSITE_TITLE") != null)
		{
			website.setTitle(parameterMap.get("WEBSITE_TITLE")[0]);
		}
		if(parameterMap.get("WEBSITE_URL") != null)
		{
			website.setVanityUrl(parameterMap.get("WEBSITE_URL")[0]);
		}
		if(parameterMap.get("WEBSITE_FOOTER") != null)
		{
			website.setFooter(parameterMap.get("WEBSITE_FOOTER")[0]);
		}
		if(parameterMap.get("WEBSITE_CSS") != null)
		{
			website.setCss(parameterMap.get("WEBSITE_CSS")[0]);
		}
		
		return website;
	}
}
