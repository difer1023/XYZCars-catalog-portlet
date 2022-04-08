package com.xyzcars.catalog.portlet;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.xyzcars.catalog.api.XYZCarsCatalogApi;
import com.xyzcars.catalog.constants.XYZCarsCatalogPortletKeys;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author difer
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=XYZCarsCatalog",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/XYZCars-catalog/view.jsp",
		"javax.portlet.name=" + XYZCarsCatalogPortletKeys.XYZCARS_CATALOG,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"javax.portlet.init-param.add-process-action-success-action=false",
		"com.liferay.portlet.css-class-wrapper=xyzCarsCatalog"
	},
	service = Portlet.class
)
public class XYZCarsCatalogPortlet extends MVCPortlet {
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		String page = (String) renderRequest.getAttribute("page");
		
		if(page != null){
			include(page, renderRequest, renderResponse);
		}else {
			renderRequest.setAttribute("catalogItems", xyzCarsCatalogApi.getAllProducts());
			include(viewTemplate, renderRequest, renderResponse);
		}
	}
	
	@ProcessAction (name="getProductDetail")
	public void getProductDetail(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		long productId= ParamUtil.getLong(actionRequest, "productId");
		actionRequest.setAttribute("catalogItem", xyzCarsCatalogApi.getProductById(productId));
		
		actionRequest.setAttribute("page","/XYZCars-catalog/product-detail.jsp");
	}
	
	@ProcessAction (name="saveAskProductForm")
	public void saveAskProductForm(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		JSONObject request = JSONFactoryUtil.createJSONObject();
		long productId= ParamUtil.getLong(actionRequest, "productId");
		request.put("productId", productId);
		request.put("customerName", ParamUtil.getString(actionRequest, "customerName"));
		request.put("customerLastName", ParamUtil.getString(actionRequest, "customerLastName"));
		String [] customerQuestions = ParamUtil.getParameterValues(actionRequest, "customerQuestions");
		
		request.put("customerQuestions", JSONFactoryUtil.createJSONArray(customerQuestions));
		
		actionRequest.setAttribute("catalogItem", xyzCarsCatalogApi.getProductById(productId));
		
		JSONObject response = xyzCarsCatalogApi.saveCustomerProductRequest(request);
		if(response.has("id")) {
			actionRequest.setAttribute("message", LanguageUtil.format(PortalUtil.getHttpServletRequest(actionRequest), "msg-ask-for-product-success", new String[] {response.getString("id")}));
		}else {
			actionRequest.setAttribute("message", "Ha ocurrido un error, por favor intente de nuevo.");
		}
		
		actionRequest.setAttribute("page","/XYZCars-catalog/product-detail.jsp");
	}
	
	@Reference
	private XYZCarsCatalogApi xyzCarsCatalogApi;
}