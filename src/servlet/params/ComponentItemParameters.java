package servlet.params;

import javax.servlet.http.HttpServletRequest;

import org.sw.marketing.data.website.Data.Website;
import org.sw.marketing.data.website.Data.Website.Page.Component;
import org.sw.marketing.data.website.Data.Website.Page.Component.Item;
import org.sw.marketing.data.website.Data.Website.Template;


public class ComponentItemParameters
{
	public static Item process(HttpServletRequest request, Item item)
	{
		@SuppressWarnings("unchecked")
		java.util.Map<String, String[]> parameterMap = (java.util.HashMap<String, String[]>) request.getAttribute("parameterMap");

		if(parameterMap.get("COMPONENT_ITEM_TITLE") != null)
		{
			item.setTitle(parameterMap.get("COMPONENT_ITEM_TITLE")[0]);
		}
		if(parameterMap.get("COMPONENT_ITEM_HTML") != null)
		{
			item.setHtml(parameterMap.get("COMPONENT_ITEM_HTML")[0]);
		}
		
		return item;
	}
}
