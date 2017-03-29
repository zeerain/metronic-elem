/**
 * 统计图表页面scripts
 * @author mazc
 */
(function($) {
	var colors = ["#3c8dbc", "#336699"];
	var gridOption = {hoverable: true, borderWidth: 1, borderColor: "#e1e1e1", tickColor: "#f3f3f3", backgroundColor: { colors: ["#ffffff", "#EDF5FF"]}};
	var singelBarOptions = {
      grid: gridOption,
      series: { bars: {show: true, barWidth: 0.5, align: "center"} },
      xaxis: {mode: "categories", tickLength: 0, transform: null, inverseTransform: null}
    };
	
	var multipleBarsOptions = {
	    series: {bars: {show: true}},
	    bars: {align: "left",barWidth: 1},
	    xaxis: {axisLabel: "", axisLabelUseCanvas: true, axisLabelFontSizePixels: 12, axisLabelFontFamily: 'Verdana, Arial', axisLabelPadding: 3},
	    yaxis: {axisLabel: "", axisLabelUseCanvas: true, axisLabelFontSizePixels: 12, axisLabelFontFamily: 'Verdana, Arial', axisLabelPadding: 3},
	    legend: {noColumns: 0, labelBoxBorderColor: "#e1e1e1", position: "nw"},
	    grid: gridOption
	};
	// 显示一个柱状图
	$.fn.showBar = function(label, barData, index){
		var bar_data = {
			label: label,
	      	data: barData,
	      	color: colors[index]
	    };
	    $.plot("#"+$(this).attr("id"), [bar_data], singelBarOptions);
	}
	// 显示多个柱状图
	$.fn.showBars = function(ticks, barDataArray, axisLabelArray){
		multipleBarsOptions.xaxis.ticks = ticks;
		multipleBarsOptions.xaxis.axisLabel = axisLabelArray[0];
		multipleBarsOptions.yaxis.axisLabel = axisLabelArray[1];
	    $.plot("#"+$(this).attr("id"), barDataArray, multipleBarsOptions);
	}
	
	$.fn.showPie = function(data){
		$.plot("#"+$(this).attr("id"), data, {
	        series: {pie: {   
                show: true,  
                radius: 1, //半径  
                label: {  
                    show: true,  
                    radius: 2/3,  
                    formatter: function(label, series){  
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';  
                    },  
                    threshold: 0.03  //这个值小于0.03，也就是3%时，label就会隐藏  
                }
	        }},
	        legend: {show: false}
		});
	}
	
	
})(jQuery);