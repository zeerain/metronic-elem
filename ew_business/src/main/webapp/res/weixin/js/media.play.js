function AudioMedia(){
    this.init();
}

AudioMedia.prototype = {
    init: function(){
        this.bindEvent();

        this.$playBtn = $(this.playBtn);

        this.playingClass = "doing";
        this.pauseClass = "pause";
        this.stopClass = "stop";
        this.$parentBox = {};
        this.lp = 0;
        this.allL = 0;
        this.$marker = {};
        this.src = '';
        this.audioObj = {};
    },
    bindEvent: function(){
        var _this = this;
        $(document).delegate(".media-ctrl", "click", function(){
            _this.$parentBox = $(this).closest('.media-bar');
            _this.$marker = _this.$parentBox.find('.marker');
            _this.lp = _this.$marker.data('lp');
            _this.allL = _this.$parentBox.find(".media-progress").width();
            _this.src = _this.$parentBox.data('src');
            if ($(this).hasClass(_this.stopClass)){
                _this.play();
                $(this).addClass(_this.playingClass).removeClass(_this.stopClass);
            } else if ($(this).hasClass(_this.pauseClass)){
                _this.playAfterPause();
                $(this).addClass(_this.playingClass).removeClass(_this.pauseClass);
            } else if ($(this).hasClass(_this.playingClass)){
                _this.pause();
                $(this).addClass(_this.pauseClass).removeClass(_this.playingClass)
            }
        });
    },
    play: function(){
        var _this = this;
        if (this.audio){
            _this.allEndInit();
            this.audio.pause();
            this.currentTime = 0.0;
        }

        this.audio = new Audio(this.src);
        this.audio.play();
        this.duration = this.audio.duration;
        if (!isNaN(this.duration)){
            this.$marker.data("duration", this.duration);
        } else {
            this.duration = this.$marker.data("duration");
        }
        this.audio.ontimeupdate = function(){
            var currentTime = _this.audio.currentTime;
            _this.changeMarker(currentTime);
            if (_this.audio.ended){
                _this.endPlay();
            }
        }
    },
    playAfterPause: function(){
        this.audio.play();
    },
    pause: function(){
        this.audio.pause();
    },

    changeMarker: function(currentTime){
        var allL = this.allL;
        var sL = -this.lp;
        var currentL = (currentTime/this.duration) * allL;
        if (currentL < allL + sL){
            this.$marker.css('left', (currentL + sL));
        }
    },

    endPlay: function(){
        this.$marker.css('left', -(this.$marker.data('lp')));
        this.$parentBox.find('.media-ctrl').addClass("stop").removeClass("doing");
    },

    allEndInit: function(){
        $(".media-ctrl").addClass("stop").removeClass("doing");
        var lp = $(".media-bar .marker").eq(0).data('lp');
        $(".media-bar .marker").css('left', -lp);
    }
}

function WexinTypeAudio(){
    this.init();
}

WexinTypeAudio.prototype = {
    init: function(){
        this.bindEvent();
    },
    bindEvent: function(){
        var _this = this;
        $(document).delegate('.cm-media', 'click', function(){
            _this.$dom = $(this);
            if ($(this).hasClass("stop")){
                _this.audio && _this.stopAll();
                if ($(this).data('src')){
                    _this.play($(this).data('src'));
                }
                $(this).addClass("play").removeClass("stop");
            } else if ($(this).hasClass("play")){
                _this.stop();
                $(this).addClass("stop").removeClass("play");
            }
        });
    },
    play: function(src){
        var _this = this;
        this.audio = new Audio(src);
        this.audio.ontimeupdate = function(){
            if (_this.audio.ended){
                _this.$dom.addClass("stop").removeClass("play");
            }
        }
        this.audio.play();
    },
    stop: function(){
        this.audio.pause();
        this.audio.currentTime = 0.0;
    },
    stopAll: function(){
        this.stop();
        $(".cm-media").addClass("stop").removeClass("play");
    }
}