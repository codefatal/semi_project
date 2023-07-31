window.onload = function() {
    priceAjax();
    
    document.getElementById("coinSelect").addEventListener('change', priceAjax);

    function priceAjax() { // 가격 정보를 받아오기 위한 Ajax
        var httpRequest = new XMLHttpRequest();
        var coinCD = getCoinCode();

        httpRequest.onreadystatechange = replaceContents;
        httpRequest.open('GET', 'coin/prices?coinCode=' + coinCD);
        httpRequest.send();

        function replaceContents() {
            if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                    document.getElementById("priceTable").innerHTML = httpRequest.response;
                    drawChart ();
                } else {
                    alert('Request Issue!');
                }
            }
        }
        
    }

    function getCoinCode() { // 현재 Select된 코인 코드 가져옴
        var e = document.getElementById("coinSelect");
        var cd = e.options[e.selectedIndex].value;
        return cd;
    }
}

function drawChart () { // 차트를 그리기 위한 함수
    var myChart = echarts.init(document.getElementById('chart'));
    
    var priceDataList = document.getElementsByClassName('priceData');
    var priceVolumeList = document.getElementsByClassName('priceVolume');
    var priceDateList = document.getElementsByClassName('priceDate');
    
    var pList = new Array();
    var vList = new Array();
    var dList = new Array();
    for(var i=0; i < priceDataList.length; i++){
        pList[i] = priceDataList[priceDataList.length-1-i].textContent;
        vList[i] = priceVolumeList[priceDataList.length-1-i].textContent;
        dList[i] = priceDateList[priceDataList.length-1-i].textContent;
    }
    
    option = {
              tooltip: {
                trigger: 'axis',
                axisPointer: {
                  type: 'cross',
                  crossStyle: {
                    color: '#999'
                  }
                }
              },
              toolbox: {
                feature: {
                  magicType: { show: true, type: ['line', 'bar'] },
                  saveAsImage: { show: true }
                }
              },
              legend: {
                data: ['거래량', '가격']
              },
              xAxis: [
                {
                  type: 'category',
                  data: dList,
                  axisPointer: {
                    type: 'shadow'
                  }
                }
              ],
              yAxis: [
                {
                  type: 'value',
                  name: '거래량',
                  min: 'dataMin',
                  max: 'dataMax',
                  sacle: true
                },
                {
                  type: 'value',
                  name: '가격',
                  min: 'dataMin',
                  max: 'dataMax'
                }
              ],
              series: [
                {
                  name: '거래량',
                  type: 'bar',
                  data: vList
                },
                {
                  name: '가격',
                  type: 'line',
                  yAxisIndex: 1,
                  data: pList
                }
              ]
            };
    myChart.setOption(option);
    
    window.onresize = function() {
    	myChart.resize();
  	};
}