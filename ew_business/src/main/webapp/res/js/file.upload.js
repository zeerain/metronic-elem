/**
 * @fileOverview HTML5 Multi Files Compress&Upload Component
 * @depends
 * @author mazhicheng
 * @date 2015-10-01
 * @version 1.0
 * @interface
 * @update
 */
(function(win, doc) {
  var MultiFile = function(obj, opts) {
    this.obj = obj;
    this.opts = opts;
    this.container = this.createContainer();
    this.placeholder = $('.placeholder', this.container)[0];
    this.listWrap = $('.filelist', this.container)[0];
    this.status = $('.status', this.container)[0];
    this.file = $('input', this.container);
    this.progress = $('.status .progress', this.container)[0];
    this.info = $('.status .info', this.container)[0];
    this.files = [];
    this.index = 0;
    this.bindEvent();
  };
  MultiFile.prototype = {
    bindEvent: function() {
      var _this = this;
      this.file.each(function() {
        var file = this;
        _this.addEvent(this, 'change', function() {
          _this.selectFile(file.files);
        });
      });
      this.addEvent(this.container, 'click', function(e) {
        var target = e.target || win.event.srcElement;
        var role = $.attr(target, 'data-role');
        var index = _this.getIndex(target.parentNode.parentNode || doc.body);
        switch (role) {
          case 'upload': _this.uploadFile(); break;
          case 'delete': _this.deleteFile(index); break;
          case 'rotateLeft': _this.rotate(index, -1); break;
          case 'rotateRight': _this.rotate(index, 1); break;
          default :break;
        }
      });
      this.addEvent(doc, 'dragleave', function(e) {e.preventDefault();});
      this.addEvent(doc, 'drop', function(e) {e.preventDefault();});
      this.addEvent(doc, 'dragenter', function(e) {e.preventDefault();});
      this.addEvent(doc, 'dragover', function(e) {e.preventDefault();});
      this.addEvent(this.container, 'drop', function(e) {
        e.preventDefault();
        _this.selectFile(e.dataTransfer.files);
      });
    },
	addEvent: function (node, type, fn) {
		if (node.addEventListener) {
			node.addEventListener(type, fn, false);
		} else if (node.attachEvent) {
			node.attachEvent('on' + type, fn);
		} else {
			node['on' + type] = fn;
		}
	},
	getIndex: function(element) {
		var index = 0;
		var nodes = this.getChild(element.parentNode);
		for(var i = 0, len = nodes.length; i < len; i++) {
			if(nodes[i] === element) {
				index = i;
				break;
			}
		}
		return index;
	},
	getChild: function(element) {
		var childes = element.childNodes;
		var list = [];
		for(var i = 0, len = childes.length; i < len; i++){
			var child = childes[i];
			if(child.nodeName !== '#text' || /\s/.test(child.nodeValue)) {
				list.push(child);
			}
		}
		list.each = function (fn) {
			$.each.call(list, fn);
		};
		return list;
	},
    changeArray: function(list) {
      var arr = [];
      for(var i = 0, len = list.length; i < len; i++) {
        arr.push(list[i]);
      }
      return arr;
    },
    changeInfo: function() {
      this.info.innerHTML = '选中'+this.files.length+'张图片';
    },
    changePanel: function(f1, f2) {
      this.placeholder.style.display = f1;
      this.listWrap.style.display = f2;
      this.status.style.display = f2;
    },
    checkType: function(type) {
      var types = this.opts.fileType.split(';');
      var obj = {};
      for(var i = 0, len = types.length; i < len; i++) {
        var key = types[i].split('.')[1];
        if(typeof key !== 'undefined') {
          obj[key] = true;
        }
      }
      return obj[type.split('/')[1]];
    },
    createContainer: function() {
      var placeholder = '<div class="placeholder"><div class="wrap"><div class="add"><label>添加图片</label><input type="file" multiple="multiple" /></div><p class="tips">最多可添加'+this.opts.maxCount+'张图片</p></div></div>';
      var fileList = '<ul class="filelist"></ul>';
      var status = '<div class="status"><div class="btns"><div class="add"><label>继续添加</label><input type="file" multiple="multiple" /></div></div><div class="progress"><span class="text">0%</span><span class="percentage"></span></div><div class="info"></div></div>';
      var container = doc.createElement('div');
      container.className = 'x-multifile-wrap';
      container.innerHTML = placeholder+fileList+status;
      container.style.cssText = 'width:'+this.opts.width+'px;height:'+this.opts.height+'px;';
      this.obj.appendChild(container);
      return container;
    },
    createFile: function(name, url, tn_width, tn_height) {
      var img = '<p class="img" ><img name="'+name+'" src="'+url+'" style="width:'+ this.opts.tnWidth +'px; height:'+ this.opts.tnHeight +'px"/></p>';
      var title = '<p class="title"><a data-role="delete"><i class="fa fa-times"></i> 移除图片</a></p>';
      var result = '<span class="result"></span>';
      var li = doc.createElement('li');
      li.innerHTML = img+title+result;
      li.style.cssText = 'width:'+(this.opts.tnWidth+6)+'px;height'+(this.opts.tnHeight+4)+'px;';
      this.listWrap.appendChild(li);
      this.fillList = $('.filelist li', this.container);
    },
    deleteFile: function(index) {
    	var fileName = this.files[index].name;
    	$('.filelist li:eq("'+index+'")', this.container).remove();
    	this.files.splice(index, 1);
    	if(this.files.length === 0) {
    		this.changePanel('block', 'none');
    	}
    	this.changeInfo();
    	this.opts.deleteFileCallback && this.opts.deleteFileCallback(fileName);
    },
    rotate: function(index, direction) {
      var img = $('.img', this.fillList[index])[0];
      var angle = $.data(img, 'angle') || 0;
      angle += direction*90;
      img.style.transform = 'rotate('+angle+'deg)';
      img.style.webkitTransform = 'rotate('+angle+'deg)';
      $.data(img, 'angle', angle);
    },
    selectFile: function(fs) {
      fs = this.changeArray(fs);
      var cLen = fs.length;
      var tLen = this.files.length;
      var max = this.opts.maxCount;
      if(tLen >= max){
    	  $.showWarning("您已经添加了"+max+"张图片，无法再添加！");
      }
      if(tLen+cLen > max) {
        fs.splice(max-tLen-cLen, tLen+cLen-max);
      }
      for(var i = 0; i < fs.length; i++) {
        var file = fs[i];
        if(this.checkType(file.type)) {
          this.files.push(file);
          (function(file, _this) {
        	  _this.uploadImageFileToServer(file);
          })(file, this);
        }
      }
      if(this.files.length > 0) {
        this.changePanel('none', 'block');
        this.changeInfo();
		this.opts.selectFileCallback && this.opts.selectFileCallback();
      }
    },
    
	// 上传图片文件到服务器
	uploadImageFileToServer: function(file){
		document.uploadingTasks++;
		var _this = this;
		// 上传文件到服务器压缩
		var form 
		var data = new FormData();
		data.append("image", file);
		var ajaxPostUrl = this.opts.uploadUrl;
		$.ajax({
		    url: ajaxPostUrl,
		    data: data,
		    cache: false,
		    contentType: false,
		    processData: false,
		    type: 'POST'
		})
		.done(function(result){
	    	if(result && result.link){
	    		document.imageUrls.push(file.name+"__"+result.link);
	    		
	    		var newLink = result.link;
	    		if(document.contextPath == "/"){
	    			newLink = document.contextPath + result.link;
				}
				else{
					newLink = document.contextPath +"/"+ result.link;
				}
	    		_this.createFile(file.name, newLink);
			}
			else if(result && result.errors){
    			var html = result.errors.join("<br/>");
    			$.showWarning(html);
    		}
	    	document.uploadingTasks--;
		})
		.fail(function(error){
			document.uploadingTasks--;
			$.showWarning("上传图片失败，图片大小不能超过5M，请尝试更换其他图片重试！");
		});
	},
	
    uploadComplete: function(e) {
      var file = this.files[this.index];
      if(file.packet && file.breakpoint < file.packet.length - 1) {
        file.breakpoint++;
        this.uploadFile();
      } else {
        var result = $.query('.result', this.fillList[this.index])[0];
        result.style.display = 'inline-block';
        result.innerHTML = '&#xf00b2;';
        if(this.index === this.files.length - 1) {
          this.opts.allCompleteCallback && this.opts.allCompleteCallback(e);
          this.progress.style.display = 'none';
          this.info.innerHTML = '共'+this.files.length+'张图片，已上传'+(this.index+1)+'张';
        } else {
          this.index++;
          this.uploadFile();
          this.info.innerHTML = '正在上传'+(this.index+1)+'/'+this.files.length+'张图片';
        }
        var percentComplete =  Math.floor((this.index/this.files.length)*100)+'%';
        this.progress.innerHTML = '<span class="text">'+percentComplete+'</span><span class="percentage" style="width:'+percentComplete+'"></span>';
        this.opts.uploadCompleteCallback && this.opts.uploadCompleteCallback(e);
      }
    },
    uploadFailed: function(e) {
      this.opts.uploadErrorCallback && this.opts.uploadErrorCallback(e);
    },
    uploadFile: function() {
      var xhr = new XMLHttpRequest();
      var _this = this;
      xhr.upload.addEventListener('progress', function(e) {_this.uploadProgress(e);}, false);
      xhr.addEventListener('load', function(e) {_this.uploadComplete(e);}, false);
      xhr.addEventListener('error', function(e) {_this.uploadFailed(e);}, false);
      xhr.open('post', this.opts.uploadUrl, true);
      var fd = new FormData();
      var file = this.files[this.index];
      var fieldName = this.opts.fieldName;
      if(file.packet) {
        fd.append('breakpoint', file.breakpoint);
        fd.append('len', file.packet.length);
        fd.append('fileName', file.name);
        fd.append('tag', file.tag);
        fd.append(fieldName, file.packet[file.breakpoint]);
      } else {
        fd.append(fieldName, file);
      }
      for(var name in this.opts.extraData) {
        fd.append(name, this.opts.extraData[name]);
      }
      xhr.send(fd);
      this.progress.style.display = 'inline-block';
    },
    uploadProgress: function(e) {
      if(e.lengthComputable) {
        var progressList = $('.filelist .progress span', this.container);
        var percentage = Math.round(e.loaded * 100 / e.total);
        var file = this.files[this.index];
        if(file.packet) {
          percentage = Math.round((file.breakpoint * e.total + e.loaded) * 100 / (file.packet.length*e.total));
        }
        progressList[this.index].style.width = percentage +'%';
      }
    }
  };
  var list = [];
  $.multiFile = function (options) {
    options = $.extend($.multiFile.defualts, options);
    var element = $(options['selector'])[0];
    element = $.isArray(element) ? element : [element];
    for (var i = 0, len = element.length; i < len; i++) {
      list.push(new MultiFile(element[i], options));
    }
  };
  $.multiFile.defualts = {
    selector: '#images-wrapper',//容器选择器
    height: 170,//容器高度
    tnWidth: 80,//预览图宽度
    tnHeight: 60,//预览图高度
    maxImgWidth: 640,//压缩图最大宽度
    maxImgHeight: 1024, //压缩最大高度
    maxCount: 6,//最大上传限制
    uploadUrl: '/mblog/saveimage',//上传地址
    fieldName: 'file',//提交时的表单名称
    fileType: '*.gif;*.png;*.jpg;*.jpeg;',
    selectFileCallback: function(){
		//console.debug("选择文件的回调");
	},// 选择文件的回调
    deleteFileCallback: function(fileName){
		//console.debug("删除某个文件的回调" + fileName);
    	var activeUrls = [];
    	for(var i=0; i<document.imageUrls.length; i++){
			var fileUrl = document.imageUrls[i];
			if(fileUrl.indexOf(fileName+"__") == -1){
				activeUrls.push(fileUrl);
			}
		}
    	document.imageUrls = activeUrls;
	},// 删除某个文件的回调
    exceedFileCallback: function(){
		//console.debug("文件超出限制的最大体积时的回调");
	},// 文件超出限制的最大体积时的回调
    startUploadCallback: function(){
		//console.debug("开始上传某个文件时的回调");
	},// 开始上传某个文件时的回调
    uploadCompleteCallback: function(){
		//console.debug("某个文件上传完成的回调");
	},// 某个文件上传完成的回调
    uploadErrorCallback: function(){
		//console.debug("某个文件上传失败的回调");
	},// 某个文件上传失败的回调
    allCompleteCallback: function(){
		//console.debug("全部上传完成时的回调");
	}// 全部上传完成时的回调
  };
})(window, document);