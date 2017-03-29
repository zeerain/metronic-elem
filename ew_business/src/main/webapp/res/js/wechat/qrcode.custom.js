
String.prototype.replaceAll=function(a,b){
	return this.replace(new RegExp(a.replace(/([\(\)\[\]\{\}\^\$\+\-\*\?\.\"\'\|\/\\])/g,"\\$1"),"ig"),b)
};
function QRCode(){
	
}
QRCode.prototype = {
	show: function(){
		if ($("#fname").val().replaceAll(" ","") != "" && $("#mobilePhone").val().replaceAll(" ","") != "") {
			this.createvcf();
		} 
	},
	generate: function(){
		var _self = this;
		$(document).on('click', '#doGenerate', function(){
			alert(1);
			_self.doGenerate();
		});
	},
	doGenerate: function(){
		var _self = this;
		var name = $("#realname").val();
		var phone = $("#mobilePhone").val();
		if(!name || !phone){
			alert("请填写客户姓名及手机号!");
			return false;
		}
		this.makeGenerate();
	},
	createvcf: function(){
		var a,b=$("#fname").val().replaceAll(" ",""), // 姓氏
		c=$("#name").val().replaceAll(" ",""), // 名字
		
		d=$("#title").val().replaceAll(" ",""), // 职称
		e=$("#organization").val().replaceAll(" ",""), // 公司/组织名称
		f=$("#orgAddress").val().replaceAll(" ",""), // 公司住址
		g=$("#orgTelphone").val().replaceAll(" ",""), // 公司电话
		h=$("#webSite").val().replaceAll(" ",""), // 个人网址
		
		i=$("#mobilePhone").val().replaceAll(" ",""), // 移动电话
		j=$("#email").val().replaceAll(" ",""), // 电子邮箱
		k=$("#homeAddress").val().replaceAll(" ",""), // 家庭住址
		l=$("#homeTelphone").val().replaceAll(" ",""), // 家庭电话
		m=$("#birthday").val().replaceAll(" ",""), // 生日
		n=$("#photo").val().replaceAll(" ",""),
		o=$("#otherPhone").val().replaceAll(" ",""); // 助理电话
		
		a="BEGIN:VCARD",
		//a+="\r\nN:"+b+";"+c+";;;",
		a+="\r\nFN: "+c+"  "+b,
		
		d&&(a+="\r\nTITLE:"+d),
		e&&(a+="\r\nORG:"+e),
		f&&(a+="\r\nADR;WORK:;;"+f+";;;;"),
		g&&(a+="\r\nTEL;WORK,VOICE:"+g),
		h&&(a+="\r\nURL;WORK:"+h),
		
		i&&(a+="\r\nTEL;PREF,VOICE:"+i),
		o&&(a+="\r\nTEL;CELL,VOICE:"+o),
		//o&&(a+="\r\nAGENT;VALUE:"+o),
		
		j&&(a+="\r\nEMAIL;INTERNET,HOME:"+j),
		k&&(a+="\r\nADR;HOME:;;"+k+";;;;"),
		l&&(a+="\r\nTEL;HOME,VOICE:"+l),
		m&&(a+="\r\nBDAY:"+m),
		
		//n&&(a+="\r\nPHOTO;VALUE=URL;TYPE=GIF:"+n),
		//o&&(a+="\r\nNOTE:"+o),
		a+="\r\nEND:VCARD",
		
		$("#qrcode").empty().qrcode({
			render:"image",
			ecLevel:"L",
			size:$("#ecardSize").val(),
			background:"#fff",
			fill:$("#ecardFill").val(),
			radius:$("#ecardRadius").val(),
			mode:$("#ecardMode").val(),
			fontcolor:$("#ecardFontcolor").val(),
			label:b+c,
			text:a
		}), 
		window.scrollTo(0,1e3)
	},
	makeGenerate: function(){
		var a ="BEGIN:VCARD";
		var name = $("#realname").val().trim();
		//b=name.substr(0, 1); // 姓氏
		//c=name.substr(1); // 名字
		a+="\r\nFN: "+name;
		var d=$("#title").val().trim(); // 职称
		if(d){
			a+="\r\nTITLE:"+d;
		}
		var i=$("#mobilePhone").val().trim(); // 移动电话
		if(i){
			a+="\r\nTEL;PREF,VOICE:"+i;
		}
		var email = $("#email").val().trim(); // email
		if(email){
			a+="\r\nEMAIL;INTERNET,HOME:"+email;
		}
		var e=$("#organization").val().trim(); // 公司/组织名称
		if(e){
			a+="\r\nORG:"+e;
		}
		var f=$("#orgAddress").val().trim(); // 公司住址
		if(f){
			a+="\r\nADR;WORK:;;"+f+";;;;";
		}
		var g=$("#orgTelphone").val().trim(); // 公司电话
		if(g){
			a+="\r\nTEL;WORK,VOICE:"+g;
		}
		
		a+="\r\nEND:VCARD";
		
		$("#qrcode").empty().qrcode({
			render:"image",
			ecLevel:"L",
			size: "${(ecard.ecardSize)!'160'}",
			background:"#fff",
			fill: "${(ecard.ecardFill)!'#000'}",
			radius: "${(ecard.ecardRadius)!'0.25'}",
			mode: "${(ecard.ecardMode)!'2'}",
			fontcolor: "${(ecard.ecardFontcolor)!'#000'}",
			label: name,
			text:a
		}), 
		window.scrollTo(0, 1e3);
	}
}