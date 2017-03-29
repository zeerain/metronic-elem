;(function(){
    var point = []; // 结果
    var bigR, smR, perimeter;
    var sum = 36; // 定义全部有多少个圆圈
    var drawNum = 0;
    circleMargin = 0.4;

    var percent = 0; // 绘制百分比(从数据中获得)

    var _PI = Math.PI;

    $(function(){
        bigR = $(".footer-cell .clac").width()/2;
        $('.canvas').width(bigR*2);
        perimeter = _PI * bigR * 2;
        smR = perimeter / (sum * 2) - circleMargin;
        getPoint(bigR, bigR, bigR, sum);
        console.log(point)
        // drawCanvas();
        appendPoint();
    });

    function appendPoint(){
        $('.canvas-box').each(function(){
            var percent = $(this).data('percent');
        var drawNum = (percent/100) * point.length;
            var output = '';
            smR = Math.floor(smR);
            for (var i = 0; i < Math.ceil(drawNum); i++){
                if (Math.abs(point[i]['x']) < 1){
                    point[i]['x'] = 0;
                } else if (Math.abs((point[i]['x'] - bigR)) < 1){
                    point[i]['x'] = bigR;
                }
                if (Math.abs(point[i]['y']) < 1){
                    point[i]['y'] = 0;
                } else if (Math.abs((point[i]['y'] - bigR)) < 1){
                    point[i]['y'] = bigR;
                }

                var pointX;
                var pointY;
                if (i == 0){
                    pointX = bigR;
                    pointY = -smR/2;
                } else {
                    pointX = point[i-1]['x'];
                    pointY = point[i-1]['y'];
                }
                var left = (pointX - smR);
                var top = (pointY - smR);
                if (left < bigR){
                    left = left - smR;
                }
                if (left > bigR){
                    left = left + smR;
                }
                if (top < bigR){
                    top = top - smR;
                }
                if (top > bigR){
                    top = top + smR;
                }
                output += '<span class="point" style="width: '+ smR*2 +'px; height: '+ smR*2 +'px; left: '+ left +'px; top: '+ top +'px"></span>';
            }
            $(this).append(output);
        });
    }

    function drawCanvas(){
        $('.canvas').each(function(){
            var canvas = this;
            var ctx = canvas.getContext("2d");
            for (var i = 0; i < point.length; i++){
                ctx.beginPath();
                ctx.strokeStyle = "black";
                var circle = {
                    x : point[i]['x'],
                    y : point[i]['y'],
                    r : smR
                };
                console.log(circle.x, circle.y, circle.r);
                 //沿着坐标点(100,100)为圆心、半径为50px的圆的顺时针方向绘制弧线
                ctx.arc(circle.x, circle.y, circle.r, 0, Math.PI / 2, false);    
                //按照指定的路径绘制弧线
                ctx.stroke();
            }
        })
    }
    
    /*
    * 求圆周上等分点的坐标
    * ox,oy为圆心坐标
    * r为半径
    * count为等分个数
    */
    function getPoint(r, ox, oy, count){
        var radians = (Math.PI / 180) * Math.round(360 / count), //弧度
            i = 0;
        for(; i < count; i++){
            var x = ox - r * Math.sin(radians * i),
                y = oy - r * Math.cos(radians * i);
            point.unshift({x:x,y:y}); //为保持数据顺时针
        }
    }
})();