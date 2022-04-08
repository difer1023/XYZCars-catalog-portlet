<%@ include file="init.jsp" %>

<%JSONArray catalogItems = (JSONArray) request.getAttribute("catalogItems"); %>

<portlet:actionURL var="actionURL" name="getProductDetail">
</portlet:actionURL>

<div class="row">
	<%for(int i=0;i<catalogItems.length();i++) {%>
	<div class="card col-12 col-md-6 col-lg-3">
	  <img src="<%=catalogItems.getJSONObject(i).getString("imageUrl") %>" class="card-img-top">
	  <div class="card-body" aria-role="button" data-productId="<%=catalogItems.getJSONObject(i).getString("id") %>">
	    <h5 class="card-title"><%=catalogItems.getJSONObject(i).getString("name") %></h5>
	  </div>
	</div>
	<%} %>
</div>
<form id="<portlet:namespace />productForm" action="<%=actionURL %>" method="post">
	<input type="hidden" name="<portlet:namespace />productId" id="<portlet:namespace />productId">
</form>
<aui:script use="aui-base">
	A.on('click',function(element){
		A.one("#<portlet:namespace />productId").val(element._currentTarget.getAttribute('data-productId'));
		A.one("#<portlet:namespace />productForm").submit();
	},'.card-body');
</aui:script>