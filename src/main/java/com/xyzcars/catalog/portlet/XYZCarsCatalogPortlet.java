package com.xyzcars.catalog.portlet;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.xyzcars.catalog.api.XYZCarsCatalogApi;
import com.xyzcars.catalog.constants.XYZCarsCatalogApiKeys;
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
		String page = (String) renderRequest.getAttribute(XYZCarsCatalogPortletKeys.PAGE);
		
		if(page != null){
			include(page, renderRequest, renderResponse);
		}else {
			renderRequest.setAttribute(XYZCarsCatalogPortletKeys.CATALOG_ITEMS, xyzCarsCatalogApi.getAllProducts());
			include(viewTemplate, renderRequest, renderResponse);
		}
	}
	
	@ProcessAction (name="getProductDetail")
	public void getProductDetail(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		long productId= ParamUtil.getLong(actionRequest, XYZCarsCatalogApiKeys.PRODUCT_ID);
		actionRequest.setAttribute(XYZCarsCatalogPortletKeys.CATALOG_ITEM, xyzCarsCatalogApi.getProductById(productId));
		
		actionRequest.setAttribute(XYZCarsCatalogPortletKeys.PAGE,"/XYZCars-catalog/product-detail.jsp");
	}
	
	@ProcessAction (name="saveAskProductForm")
	public void saveAskProductForm(ActionRequest actionRequest, ActionResponse actionResponse)
			throws IOException, PortletException {
		JSONObject request = JSONFactoryUtil.createJSONObject();
		long productId= ParamUtil.getLong(actionRequest, XYZCarsCatalogApiKeys.PRODUCT_ID);
		request.put(XYZCarsCatalogApiKeys.PRODUCT_ID, productId);
		request.put(XYZCarsCatalogApiKeys.CUSTOMER_NAME, ParamUtil.getString(actionRequest, XYZCarsCatalogApiKeys.CUSTOMER_NAME));
		request.put(XYZCarsCatalogApiKeys.CUSTOMER_LAST_NAME, ParamUtil.getString(actionRequest, XYZCarsCatalogApiKeys.CUSTOMER_LAST_NAME));
		String [] customerQuestions = ParamUtil.getParameterValues(actionRequest, XYZCarsCatalogApiKeys.CUSTOMER_QUESTIONS);
		
		request.put(XYZCarsCatalogApiKeys.CUSTOMER_QUESTIONS, JSONFactoryUtil.createJSONArray(customerQuestions));
		
		actionRequest.setAttribute(XYZCarsCatalogPortletKeys.CATALOG_ITEM, xyzCarsCatalogApi.getProductById(productId));
		
		JSONObject response = xyzCarsCatalogApi.saveCustomerProductRequest(request);
		if(response.has(XYZCarsCatalogPortletKeys.ID)) {
			actionRequest.setAttribute(XYZCarsCatalogPortletKeys.MESSAGE, LanguageUtil.format(PortalUtil.getHttpServletRequest(actionRequest), "msg-ask-for-product-success", new String[] {response.getString(XYZCarsCatalogPortletKeys.ID)}));
		}else {
			actionRequest.setAttribute(XYZCarsCatalogPortletKeys.MESSAGE, LanguageUtil.format(PortalUtil.getHttpServletRequest(actionRequest), "msg-general-error",new String[]{}));
		}
		
		actionRequest.setAttribute(XYZCarsCatalogPortletKeys.PAGE,"/XYZCars-catalog/product-detail.jsp");
	}
	
	@Reference
	private XYZCarsCatalogApi xyzCarsCatalogApi;
}