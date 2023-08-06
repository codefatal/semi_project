$(document).ready(function () {
    $(".Orders_tabbtn").click(function (e) {
    e.preventDefault();
    var tabType = $(this).data("tab"); // 클릭한 버튼의 data-tab 속성 값을 가져옴

    // 해당 탭 버튼 활성화 처리
    $(this).parents(".Orders_tabitem").attr("aria-selected", "true")
      .siblings().attr("aria-selected", "false");

	    // 해당 탭의 내용 표시
	    if (tabType === "buy") {
	      $(".buyTabContent").show(); // 매수 탭 내용 보이기
	      $(".sellTabContent").hide(); // 매도 탭 내용 숨기기
	      $(".ButtonRowBuyContent").show(); // 매수 버튼 보이기
	      $(".ButtonRowSellContent").hide(); // 매도 버튼 숨기기
	    } else if (tabType === "sell") {
	      $(".buyTabContent").hide(); // 매수 탭 내용 숨기기
	      $(".sellTabContent").show(); // 매도 탭 내용 보이기
	      $(".ButtonRowBuyContent").hide(); // 매수 버튼 숨기기
	      $(".ButtonRowSellContent").show(); // 매도 버튼 보이기
	    }
    });

    // 매도 버튼 클릭 이벤트 처리 추가
    $(".Button_JtY.Button_large.Button_row_sell").click(function () {
	    // 매도 버튼이 클릭되면 수행할 동작을 여기에 작성하면 됩니다.
	    // 예를 들어, 매도 주문을 실행하거나 다른 동작을 수행하는 등의 코드를 추가할 수 있습니다.
	      console.log("매도 버튼이 클릭되었습니다.");
   	});
   	
});

function priceAjax() {
      var httpRequest = new XMLHttpRequest();
      var coinCD = getCoinCode();

      httpRequest.onreadystatechange = replaceContents;
      httpRequest.open('GET', 'coin/prices?coinCode=' + coinCD);
      httpRequest.send();

      function replaceContents() {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
          if (httpRequest.status === 200) {
            document.getElementById('priceTable').innerHTML = httpRequest.response;

            // placeholder 업데이트
            var priceDataList = document.getElementsByClassName('priceData');
            var firstPriceElement = priceDataList[0]; // 첫 번째 가격 데이터 요소
            var firstPrice = parseInt(firstPriceElement.textContent.replace(/,/g, ''));
            document.querySelector('.InputBox_box_input').setAttribute('value', `${firstPrice.toLocaleString()}`);

            drawChart ();
          } else {
            alert('요청 문제 발생!');
          }
        }
      }
    }

function getCoinCode() {
  var e = document.getElementById("coinSelect");
  var cd = e.options[e.selectedIndex].value;
  return cd;
}

window.onload = function() {
    priceAjax();
    
    document.getElementById("coinSelect").addEventListener('change', priceAjax);

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
        pList[i] = parseInt(priceDataList[priceDataList.length - 1 - i].textContent.replace(/,/g, ''));
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