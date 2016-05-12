package servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Page.Component;
import org.sw.marketing.data.website.Data.Website.Template;


public class ComponentParameters
{
	public static Component process(HttpServletRequest request, Component component)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("COMPONENT_TITLE") != null)
		{
			component.setTitle(parameterMap.get("COMPONENT_TITLE")[0]);
		}
		
		return component;
	}
}
