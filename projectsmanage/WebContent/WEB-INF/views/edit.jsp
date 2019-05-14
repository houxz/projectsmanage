<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>制作</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css" rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href='http://static.emapgo.cn/webjs-sdk/css/emapgo-1.0.0.css' rel='stylesheet' />
<link href="resources/js/leaflet/leaflet.css" rel="stylesheet" />

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/js/common.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src='http://static.emapgo.cn/webjs-sdk/js/emapgo-1.0.0.js'></script>
<script src="resources/js/leaflet/leaflet.js"></script>
<script src="resources/js/leaflet.ChineseTmsProviders.js"></script>
<script src="resources/js/proj4-compressed.js"></script>
<script src="resources/js/proj4leaflet.js"></script>
<script src="resources/js/tileLayer.baidu.js" ></script > 
   

<script type="text/javascript">
	var emgmap = null, baidumap = null, gaodemap = null, tengxunmap = null;
	var centerX = 116.3970060000;
	var centerY = 39.9178540000;
	var centerXB = 116.4034140000;
	var centerYB = 39.9240910000;
	var zoom = 17;
	
	$(document).ready(function() {
		$.webeditor.getHead();

		emgmap = new emapgo.Map({
	        container : 'emgmap',
	        style: 'http://tiles.emapgo.cn/styles/outdoor/style.json',
	        zoom: zoom-1,
	        center:  [centerX, centerY],
	        hash :true
	    });
		var marker = new emapgo.Marker()
			.setLngLat([centerX, centerY ])
			.addTo(emgmap);
		
	    baidumap = L.map('baidumap', {
			zoomControl : false,
			center:  [centerYB, centerXB],
			zoom: zoom+1,
			crs: L.CRS.Baidu,
			layers : [new L.tileLayer.baidu({ layer: 'custom'})]
		});
	    L.marker([centerYB, centerXB]).addTo(baidumap);

		gaodemap = L.map('gaodemap', {
			zoomControl : false,
			center:  [centerY, centerX],
			zoom: zoom,
			layers : [L.tileLayer.chinaProvider('GaoDe.Normal.Map', {})]
		});
		L.marker([centerY, centerX]).addTo(gaodemap);
		
		tengxunmap = L.map('tengxunmap', {
			zoomControl : false,
			center:  [centerY, centerX],
			zoom: zoom,
			layers : [L.tileLayer.chinaProvider('TengXun.Normal.Map', {
				subdomains: '0123',
				tms:true
			})]
		});
		L.marker([centerY, centerX]).addTo(tengxunmap);
		
	});
	
	function centerTo(centX, centY) {
		
	}
</script>
</head>
<body>
	<div id="headdiv"></div>
	<div class="containerdiv">
		<div class="row-fluid fullHeight">
			<div class="col-md-2 fullHeight">
				<div style="position: absolute; top: 0; left: 0; right: 0; height: 38%; overflow-y: scroll;">
					<table class="table table-bordered table-condensed">
						<tbody>
							<tr class="">
								<td class="tdKey">编号1</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号2</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号3</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号4</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号5</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号6</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号7</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号8</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号9</td>
								<td class="tdValue">210000002322366</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div style="position: absolute; top: 39%; left: 0; right: 0; bottom: 40px; overflow-y: scroll;">
					<table class="table table-bordered table-condensed">
						<tbody>
							<tr class="">
								<td class="tdKey">编号1</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号2</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号3</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号4</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号5</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号6</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号7</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号8</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号9</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号1</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号2</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号3</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号4</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号5</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号6</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号7</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号8</td>
								<td class="tdValue">210000002322366</td>
							</tr>
							<tr class="">
								<td class="tdKey">编号9</td>
								<td class="tdValue">210000002322366</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div style="position: absolute; left: 0; right: 0; bottom: 0; height: 38px; text-align: center;">
					<button class="btn btn-default">稍后修改</button>
					<button class="btn btn-default">保存</button>
					<button class="btn btn-default">提交</button>
				</div>
			</div>
			<div class="col-md-10 fullHeight">
				<div style="position: absolute; top: 0; left: 0; right: 0; height: 50%;">
			    	<div class="mappanel" style="position: absolute; top: 0; left: 0; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>EMG地图</strong></div>
						    <div class="panel-body">
						    	<div id="emgmap" style="height: 100%;"></div>
						    </div>
						</div>
			    	</div>
			    	<div class="mappanel" style="position: absolute; top: 0; left: 25%; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>百度地图</strong></div>
						    <div class="panel-body">
						    	<div id="baidumap" style="height: 100%;"></div>
						    </div>
						</div>
			    	</div>
			    	<div class="mappanel" style="position: absolute; top: 0; left: 50%; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>高德地图</strong></div>
						    <div class="panel-body">
						    	<div id="gaodemap" style="height: 100%;"></div>
						    </div>
						</div>
			    	</div>
			    	<div class="mappanel" style="position: absolute; top: 0; left: 75%; width: 24.8%; height: 100%;">
			    		<div class="panel panel-default">
						    <div class="panel-heading"><strong>腾讯地图</strong></div>
						    <div class="panel-body">
						    	<div id="tengxunmap" style="height: 100%;"></div>
						    </div>
						</div>
			    	</div>
				</div>
				<div style="position: absolute; left: 0; right: 0; bottom: 0; height: 50%;">
					<div class="" style="position: absolute; top: 0; left: 0; width: 24.8%; height: 100%; overflow-y: scroll;">
						<table class="table table-bordered table-condensed">
							<tbody>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd1" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd1" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
							</tbody>
						</table>
			    	</div>
			    	<div class="" style="position: absolute; top: 0; left: 25%; width: 24.8%; height: 100%; overflow-y: scroll;">
						<table class="table table-bordered table-condensed">
							<tbody>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd2" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd2" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
							</tbody>
						</table>
			    	</div>
			    	<div class="" style="position: absolute; top: 0; left: 50%; width: 24.8%; height: 100%; overflow-y: scroll;">
						<table class="table table-bordered table-condensed">
							<tbody>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd3" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd3" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
							</tbody>
						</table>
			    	</div>
			    	<div class="" style="position: absolute; top: 0; left: 75%; width: 24.8%; height: 100%; overflow-y: scroll;">
						<table class="table table-bordered table-condensed">
							<tbody>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd4" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr><td class="trIndex" rowspan="10">
									<label class="radio-inline">
								        <input type="radio" name="rd4" value="option1">
								    </label>
								</td></tr>
								<tr class="">
									<td class="tdKey">编号1</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号2</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号3</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号4</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号5</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号6</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号7</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号8</td>
									<td class="tdValue">210000002322366</td>
								</tr>
								<tr class="">
									<td class="tdKey">编号9</td>
									<td class="tdValue">210000002322366</td>
								</tr>
							</tbody>
						</table>
			    	</div>
				</div>
			</div>
		</div>
	</div>
	<div class="footline">
		<div><span>当前项目编号：</span><span id="curProcessID">${process.id}</span></div>
		<div><span>当前项目：</span><span id="curProcessName">${process.name}</span></div>
		<div><span>项目公有/私有：</span><span id="curProjectOwner">
			<c:set var="owner" value="${project.owner == 1 ? '私有' : '公有' }"/>
			<c:out value="${owner }"></c:out>
		</span></div>
		<div><span>当前任务编号：</span><span id="curTaskID">${task.id}</span></div>
		<div><input type="hidden" id="curProjectID" value="${task.projectid}"></div>
	</div>
</body>
</html>