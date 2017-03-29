$(function(){
	voice = new VoiceByWechat({
		btn_default_class: 'default',
		btn_doing_class: 'doing'
	});
	voice.init();
});

var $progress = $('.js_progress');
var progress = 0;
var step = 100/(1000/50 * 60);
function next() {
    if(progress > 100 || $btnUpload.hasClass("weui-btn_primary")){
        progress = 0;
        return;
    }
    $progress.css({width: progress + '%'});
    progress = progress + step;
    setTimeout(next, 50);
}

function VoiceByWechat(obj){
	this.isWxInit = false;

	this.localId = "";
	this.serverId = "";
	this.ctx = $("body").data("ctx");
	
	this.$progress = $(".weui-progress");
	
	
	var obj = obj || {};
	
	this.btn_default_class = obj.btn_default_class || "weui-btn_primary";
	
	this.btn_doing_class = obj.btn_doing_class || "weui-btn_warn";
	
	this.selfBtn = {};
	
	this.$parentBox = {};
	
	this.startTime = 0;
	this.duration = 0;
	
}

VoiceByWechat.prototype = {
	init: function(){
		this.bindEvent();
	},
	
	bindEvent: function(){
		var _self = this;
		$(document).delegate(".startRecord", 'touchstart', function(e){
			if ($(this).attr("disabled")){
				return false;
			}
			_self.selfBtn = this;
			_self.$parentBox = $(this).closest(".ctrl-box");
			_self.startTime = new Date().getTime();
			if(!weixin){
				alert("正在初始化录音控件，请稍后再试！");
				return false;
			}
			$(this).removeClass(_self.btn_default_class).addClass(_self.btn_doing_class).find('.statusText').html('松开&nbsp;结束');
			if(!_self.isWxInit){
				_self.wxInit();
				_self.isWxInit = true;
			}
			wx.startRecord();
			if ( e && e.preventDefault ) {
			    e.preventDefault(); 
			}
			return false;
		}).delegate(".startRecord", 'touchend', function(e){
			if ($(this).attr("disabled")){
				return false;
			}
			_self.selfBtn = this;
			wx.stopRecord({
			    success: function (res) {
			        _self.recordEnd(res);
			        _self.translate();
			    }
			});
			if ( e && e.preventDefault ) {
			    e.preventDefault(); 
			}
			return false;
		}).delegate(".btn-submit", 'click', function(){
			if ($(this).hasClass("disabled")){
				return false;
			}
			var that = this;
			$fm = $(this).closest(".audio-fm");
            var postUrl = $fm.attr("action");
            $.post(postUrl, $fm.serialize())
            .done(function(){
            	alert("语音消息发送成功！");
            	$li = $(that).closest("li");
            	$li.find(".fenfu").addClass("doing").attr("disabled", "disabled");
            	$(that).attr("disabled", "disabled").addClass("disabled");
            	if ($li.data('type') == 'task'){
               	 $li.remove();
            	}
			})
			.fail(function(){
				alert("语音消息发送失败！");
			});
		});
	},
	
	wxInit: function(){
		var _self = this;
		wx.onVoiceRecordEnd({
		    // 录音时间超过一分钟没有停止的时候会执行 complete 回调
		    complete: function (res) {
		        _self.recordEnd(res);
		    }
		});
		
		wx.onVoicePlayEnd({
		    success: function (res) {
		        var localId = res.localId; // 返回音频的本地ID
		        _self.playEnd();
		    }
		});
	},
	
	recordEnd: function(res){
		var _self = this;
		var localId = res.localId;
		this.localId = localId;
		_self.duration = Math.ceil((new Date().getTime() - _self.startTime) / 1000);
		_self.$parentBox.find(".duration").val(_self.duration);
		$(_self.selfBtn).removeClass(_self.btn_doing_class).addClass(_self.btn_default_class).find('.statusText').html('按住&nbsp;说话');
	},
	
	translate: function(){
		var _self = this;
		wx.translateVoice({
		   localId: _self.localId, // 需要识别的音频的本地Id，由录音相关接口获得
		    isShowProgressTips: 1, // 默认为1，显示进度提示
		    success: function (res) {
				_self.$parentBox.find(".content").val(res.translateResult);
		        _self.uploadVoice();
		    }
		});
	}, 
	
	uploadVoice: function(){
		var _self = this;
		if(this.localId == ""){
			alert("还没开始录音");
		}
		
		wx.uploadVoice({
		    localId: this.localId, // 需要上传的音频的本地ID，由stopRecord接口获得
		    isShowProgressTips: 1, // 默认为1，显示进度提示
		    success: function (res) {
		        var serverId = res.serverId; // 返回音频的服务器端ID
		        _self.$parentBox.find('.audioUrls').val(serverId);
		        _self.serverId = serverId;
				_self.$parentBox.find(".btn-submit").removeClass("disabled").removeAttr("disabled");
//		        _self.saveVoice();
		    }
		});
	},
	
	saveVoice: function(){
		var _self = this;
        var $pBox = this.$parentBox;
		$.ajax({
			url: _self.ctx + "/wechat/jsSdk/saveWechatVoice",
			type: 'post',
			data: $pBox.serialize(),
			dataType: 'json'
		}).done(function(res){
			if (res.status > 0 && res.audioUrls != ""){
				$pBox.find(".audioUrls").val(res.audioUrls);
				_self.$parentBox.find(".btn-submit").removeClass("disabled").removeAttr("disabled");
			}
		}).fail(function(){
			alert("上传音频失败");
		});
	}
}