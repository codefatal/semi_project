$(document).ready(function() {
    let myPageData;  // myPage 데이터를 저장하기 위한 전역 변수를 선언합니다.
    let priceLista = {};
    let priceListb = {};
    let coinMapping = {};
    
    
    const savedCoinCode = localStorage.getItem('selectedCoinCode') ?? "BTC";
	
	if (savedCoinCode) {
        $('#coinSelect').val(savedCoinCode).trigger('change');
	}
	
	console.log("Document is ready!");
	handleCoinSelection();
	
	
    // myPage 정보를 가져오는 AJAX 요청
    $.ajax({
        url: "/coin/prices/mypage",
        type: "GET",
        dataType: "json",
        success: function(response) {
			console.log(response)
            myPageData = response;
            
            // myPageData 설정 후 priceList 요청 시작
            fetchPriceListBasedOnSelectedCoin();
        },
        error: function(xhr, status, error) {
            if(xhr.status === 403) {
                console.error("User is not authenticated.");
            } else {
                console.error("Error occurred while fetching myPage data:", error);
            }
        }
    });
    
    $(document).on('change', '#coinSelect', handleCoinSelection);
    
    function handleCoinSelection() {
	    fetchPriceListBasedOnSelectedCoin();
	}
	
	function continueHandleCoinSelection() {
	    // 선택한 코인의 이름을 가져옵니다.
        const selectedCoinName = $('#coinSelect').find('option:selected').text() ?? "비트코인";
        const selectedCoinCode = coinMapping[selectedCoinName] ?? "BTC";
        const selectedCoin = $('#coinSelect').val() ?? "BTC";
        updateCoinValueDisplay(selectedCoin);

        // 버튼 이름 변경
        $('.Orders_tabbtn[data-tab="buy"]').text(selectedCoinCode + ' 매수');
        $('.Orders_tabbtn[data-tab="sell"]').text(selectedCoinCode + ' 매도');

        // BuySellTab_available-price의 내용 변경
        $('.BuySellTab_available-price').text(function() {
            return $(this).text().replace('BTC', selectedCoinCode);
        });

        // InputBox_sub-txt의 내용 변경
        $('.InputBox_sub-txt').text(selectedCoinCode);

        // Button_text의 내용 변경
        $('.Button_text_buy').text(selectedCoinCode + ' 매수');
        $('.Button_text_sell').text(selectedCoinCode + ' 매도');
        
        
        let maxQuantitySell;
        if (selectedCoinCode === "BTC") {
	        maxQuantitySell = myPageData.userBtc * priceLista[0].price;
	    } else if (selectedCoinCode === "ETH") {
	        maxQuantitySell = myPageData.userEth * priceListb[0].price;
	    }
	    
	    const truncatedSell = Math.floor(maxQuantitySell * Math.pow(10, 5)) / Math.pow(10, 5);
	    $('.maxQuantitySell').text(truncatedSell);
	    
	    resetOrderInputs();
	    localStorage.setItem('selectedCoinCode', selectedCoinCode);
	    
	    $.ajax({
	        url: "/maxquantity",
	        method: "GET",
	        data: { coinCode: selectedCoinCode },
	        success: function(data) {
	            $(".maxQuantityBuy").text(data);
	        },
	        error: function(err) {
	            console.error("Failed to fetch max quantity.", err);
	        }
	    });
	}
    
    function fetchPriceListBasedOnSelectedCoin() {
	    const selectedCoin = $(".coinSelect").val() ?? "BTC";
	    
	    switch(selectedCoin) {
	        case 'BTC':
	            fetchBtcPriceList();
	            break;
	        case 'ETH':
	            fetchEthPriceList();
	            break;
	        // 다른 코인들에 대해서도 동일하게 추가 가능
	        default:
	            console.error('Unknown coin selected.');
	            return;
	    }
	}
	
	$.ajax({
	    url: "/coin/coinlist",
	    type: "GET",
	    dataType: "json",
	    success: function(data) {
	        data.forEach(coinList => {
	            coinMapping[coinList.coinname] = coinList.coincode;
	        });

	    },
	    error: function(error) {
	        console.error("Failed to fetch coin data:", error);
	    }
	});
	
    function fetchBtcPriceList() {
        // AJAX 요청을 보내서 priceList를 가져옵니다.
        $.ajax({
		    url: "/coin/prices/priceslist",
		    type: "GET",
		    dataType: "json",
		    data: {
		        coinCode: "BTC"  // 여기에 적절한 코인 코드 값을 입력해주세요.
		    },
		    success: function(response) {
		        priceLista = response;
		        updatePageWithPriceList(response);
		        continueHandleCoinSelection();
		    },
		    error: function(xhr, status, error) {
		        console.error("Error occurred while fetching priceList:", error);
		    }
		});
    }
    
    function fetchEthPriceList() {
        // AJAX 요청을 보내서 priceList를 가져옵니다.
        $.ajax({
		    url: "/coin/prices/priceslist",
		    type: "GET",
		    dataType: "json",
		    data: {
		        coinCode: "ETH"  // 여기에 적절한 코인 코드 값을 입력해주세요.
		    },
		    success: function(response) {
		        priceListb = response;
		        updatePageWithPriceList(response);
		        continueHandleCoinSelection();
		    },
		    error: function(xhr, status, error) {
		        console.error("Error occurred while fetching priceList:", error);
		    }
		});
    }
    
	function updateCoinValueDisplay(coinCode) {
	    let value, text;
	    switch (coinCode) {
	        case "BTC":
	            value = myPageData.userBtc;
	            text = "BTC";
	            break;
	        case "ETH":
	            value = myPageData.userEth;
	            text = "ETH";
	            break;
	        // 여기에 다른 코인들도 추가 가능합니다.
	        default:
	            console.error("Unsupported coin code:", coinCode);
	            return;
	    }
	    $(".priceCoin").text(`${value} ${text}`);
	}
	
    // 탭 버튼 클릭 이벤트 추가
	$('.Orders_tabbtn').on('click', function() {
        const selectedTab = $(this).data('tab');
        resetOrderInputs();

        if (selectedTab === 'buy') {
            // 매수 버튼을 클릭했을 때의 처리
            $('.Orders_tabitem').attr('aria-selected', false);  // 모든 탭 아이템의 aria-selected 값을 false로 설정
            $(this).parent().attr('aria-selected', true);  // 클릭한 탭 아이템만 aria-selected 값을 true로 설정
        } else if (selectedTab === 'sell') {
            // 매도 버튼을 클릭했을 때의 처리
            $('.Orders_tabitem').attr('aria-selected', false);  // 모든 탭 아이템의 aria-selected 값을 false로 설정
            $(this).parent().attr('aria-selected', true);  // 클릭한 탭 아이템만 aria-selected 값을 true로 설정
        }
    });
    
    function resetOrderInputs() {
        $('#orderQuantitySecondary').val('');  // 주문 수량 초기화
        $('#orderAmount').val('');  // 주문 금액 초기화
    }
	
	// AJAX 요청을 보내서 priceList를 가져옵니다. 
    function setOrderPercentage(percentage) {
		const selectedCoinCode = $(".coinSelect").val() ?? "BTC";
		if(selectedCoinCode === "BTC") {
			$.ajax({
			    url: "/coin/prices/priceslist",
			    type: "GET",
			    dataType: "json",
			    data: {
			        coinCode: "BTC"  // 여기에 적절한 코인 코드 값을 입력해주세요.
			    },
			    success: function(response) {
			        priceLista = response;
			    },
			    error: function(xhr, status, error) {
			        console.error("Error occurred while fetching priceList:", error);
			    }
			});
		} else if(selectedCoinCode === "ETH") {
			$.ajax({
			    url: "/coin/prices/priceslist",
			    type: "GET",
			    dataType: "json",
			    data: {
			        coinCode: "ETH"  // 여기에 적절한 코인 코드 값을 입력해주세요.
			    },
			    success: function(response) {
			        priceListb = response;
			    },
			    error: function(xhr, status, error) {
			        console.error("Error occurred while fetching priceList:", error);
			    }
			});
		}
        let maxOrderQuantity;

        // 현재 선택된 탭을 확인
        const selectedTab = $('.Orders_tabitem[aria-selected="true"]').children('.Orders_tabbtn').data('tab');
        
        if (selectedTab === 'buy') {
            // 매수일 때의 계산
            if (selectedCoinCode === "BTC") {
            	maxOrderQuantity = myPageData.money / priceLista[0].price;
            	priceList = priceLista;
		    } else if (selectedCoinCode === "ETH") {
		        maxOrderQuantity = myPageData.money / priceListb[0].price;
            	priceList = priceListb;
		    }            	
        } else {
            // 매도일 때의 계산
            if (selectedCoinCode === "BTC") {
		        maxOrderQuantity = myPageData.userBtc;
		    } else if (selectedCoinCode === "ETH") {
		        maxOrderQuantity = myPageData.userEth;
		    }
        }

        const orderQuantity = (percentage / 100) * maxOrderQuantity;
        const truncatedOrderQuantity = Math.floor(orderQuantity * Math.pow(10, 5)) / Math.pow(10, 5);

        $('#orderQuantitySecondary').val(truncatedOrderQuantity.toFixed(5));
        calculateOrderAmount(priceList);
    }


    // 페이지 업데이트 함수
    function updatePageWithPriceList(priceList) {
        // 주문 수량 입력란에 내용이 변경될 때마다 주문 금액을 업데이트하는 이벤트 리스너 등록
        $('.orderQuantityInput').off('input').on('input', function() {
		    calculateOrderAmount(priceList);
		});

        // 주문 퍼센트 버튼 클릭 시 주문 수량 및 주문 금액을 업데이트하는 이벤트 리스너 등록
		$('.OrderForm_qty-per_btn').off('click').on('click', function() {
            const percentage = parseInt($(this).data('percentage'));
            setOrderPercentage(percentage);
        });
    }

    // 주문 금액 계산 함수
    function calculateOrderAmount(priceList) {
        const orderQuantity = parseFloat($('#orderQuantitySecondary').val()) || 0; // 주문 수량 값을 가져옵니다. 값이 없거나 NaN이면 0으로 설정
    	const pricePer = priceList[0] && priceList[0].price ? priceList[0].price : 0; // 코인 금액을 가져옵니다. 값이 없으면 0으로 설정

        // 주문 금액을 계산합니다.
	    const orderAmount = orderQuantity * pricePer;
	
	    // 계산된 주문 금액을 적절한 형식으로 변환합니다.
	    const formattedAmount = formatPrice(orderAmount);
	
	    // 변환된 주문 금액을 input 필드에 표시합니다.
	    $('#orderAmount').val(formattedAmount);
    }
    
    function formatPrice(price) {
	    // 소수점 이하 2자리로 반올림한 후 정수 부분만 사용
	    const integerPart = Math.round(price * 100) / 100;
	
	    // 정수 부분을 문자열로 변환하고 3자리마다 쉼표 추가
	    const withCommas = integerPart.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	
	    // '원' 문자 추가
	    return withCommas + "원";
	}
	
	// 최대치 이상 설정 금지
	$('.orderQuantityInput').on('input', function() {
		const selectedCoinCode = $(".coinSelect").val() ?? "BTC";
	    let maxOrderQuantity;
	    let rawPricePer;
		if (selectedCoinCode === "BTC") {
	        rawPricePer = priceLista[0] && priceLista[0].price ? priceLista[0].price : 0;
	    } else if (selectedCoinCode === "ETH") {
	        rawPricePer = priceListb[0] && priceListb[0].price ? priceListb[0].price : 0;
	    }
	    const pricePer = Math.floor(rawPricePer * 100000) / 100000.0;
	
	    // 매수 탭이 선택된 경우
	    if ($('.Orders_tabitem[aria-selected="true"] .Orders_tabbtn').data('tab') === "buy") {
	        maxOrderQuantity = myPageData.money / pricePer;
	    } 
	    // 매도 탭이 선택된 경우
	    else if ($('.Orders_tabitem[aria-selected="true"] .Orders_tabbtn').data('tab') === "sell") {
	        if (selectedCoinCode === "BTC") {
		        maxOrderQuantity = myPageData.userBtc;
		    } else if (selectedCoinCode === "ETH") {
		        maxOrderQuantity = myPageData.userEth;
		    }
	    }
	
	    let inputValue = parseFloat($(this).val());
	    
	    if (inputValue > maxOrderQuantity) {
	        alert("최대 주문 가능 수량을 초과했습니다.");
	        $(this).val(maxOrderQuantity);
	    }
	});
	
	// 매수 버튼 클릭 이벤트 추가
    $('.Button_row_buy').on('click', function() {
        // 주문 금액 및 주문 수량 값을 가져옵니다.
        const selectedCoinCode = $(".coinSelect").val() ?? "BTC";
        const orderAmount = parseFloat($('#orderAmount').val().replace('원', '').replace(/,/g, '')) || 0;
        const orderQuantity = parseFloat($('#orderQuantitySecondary').val()) || 0;
        let rawPricePer;
		if (selectedCoinCode === "BTC") {
	        rawPricePer = priceLista[0] && priceLista[0].price ? priceLista[0].price : 0;
	    } else if (selectedCoinCode === "ETH") {
	        rawPricePer = priceListb[0] && priceListb[0].price ? priceListb[0].price : 0;
	    }
	    const pricePer = Math.floor(rawPricePer * 100000) / 100000.0;
		
		console.log(selectedCoinCode);
        // 주문금액이 사용자의 보유금액보다 많으면 매수를 진행하지 않습니다.
        if(orderQuantity > myPageData.money) {
            alert("매수하려는 주문금액이 보유금액보다 많습니다!");
            return;
        }

        // myPage.money에서 주문금액을 빼고, myPage.userBtc에 주문수량을 더합니다.
        myPageData.money = myPageData.money - orderAmount;
        if (selectedCoinCode === "BTC") {
	        myPageData.userBtc = myPageData.userBtc + orderQuantity;
	    } else if (selectedCoinCode === "ETH") {
	        myPageData.userEth = myPageData.userEth + orderQuantity;
	    }
        
        $.ajax({
            url: "/coin/prices/buy", 
            method: "POST",
            contentType: "application/json; charset=utf-8",  // 이 부분 추가
    		dataType: "json",  // 이 부분 추가
            data: JSON.stringify ({
				coincode: selectedCoinCode,
                money: myPageData.money,
                userBtc: myPageData.userBtc,
                userEth: myPageData.userEth,
                price: pricePer,
                orderAmount: orderAmount
            }),
            success: function(response) {
                console.log("Data updated successfully!", response);
                
                // 서버 응답을 기반으로 클라이언트 상태 업데이트
	            myPageData.money = response.money;
	            myPageData.userBtc = response.userBtc;
	            myPageData.userEth = response.userEth;
	            
	            localStorage.setItem('selectedCoinCode', selectedCoinCode);
               
                // 서버로부터 받은 redirectUrl을 사용하여 페이지를 새로고침하거나 리다이렉트 수행
		        if (response.redirectUrl) {
		            window.location.href = response.redirectUrl;
		        }
            },
            error: function(err) {
                console.error("Failed to update data.", err);
            }
        });

        // 업데이트된 값을 콘솔에 출력합니다 (데모용).
        console.log("Updated myPage data:", myPageData);

        // 여기서 필요한 경우 서버에 업데이트된 데이터를 저장하거나 페이지를 갱신할 수 있습니다.
    });
    
    // 매도 버튼 클릭 이벤트 추가
    $('.Button_row_sell').on('click', function() {
        // 주문 금액 및 주문 수량 값을 가져옵니다.
        const selectedCoinCode = $(".coinSelect").val() ?? "BTC";
        const orderAmount = parseFloat($('#orderAmount').val().replace('원', '').replace(/,/g, '')) || 0;
        const orderQuantity = parseFloat($('#orderQuantitySecondary').val()) || 0;
		let rawPricePer;
		if (selectedCoinCode === "BTC") {
	        rawPricePer = priceLista[0] && priceLista[0].price ? priceLista[0].price : 0;
	    } else if (selectedCoinCode === "ETH") {
	        rawPricePer = priceListb[0] && priceListb[0].price ? priceListb[0].price : 0;
	    }
	    const pricePer = Math.floor(rawPricePer * 100000) / 100000.0;
		
		
        // 주문 수량이 사용자의 코인 보유량보다 많으면 매도를 진행하지 않습니다.
        if(selectedCoinCode === "BTC") {
	        if(orderQuantity > myPageData.userBtc) {
	            alert("매도하려는 비트코인 수량이 보유량보다 많습니다!");
	            return;
	        }
		} else if(selectedCoinCode === "ETH") {
	        if(orderQuantity > myPageData.userEth) {
	            alert("매도하려는 이더리움 수량이 보유량보다 많습니다!");
	            return;
	        }			
		}

        // myPage.money에 주문금액을 더하고, myPage.userBtc에서 주문수량을 뺍니다.
        myPageData.money = myPageData.money + orderAmount;
        if (selectedCoinCode === "BTC") {
	        myPageData.userBtc = myPageData.userBtc - orderQuantity;
	    } else if (selectedCoinCode === "ETH") {
	        myPageData.userEth = myPageData.userEth - orderQuantity;
	    }
        
        $.ajax({
            url: "/coin/prices/sell", 
            method: "POST",
            contentType: "application/json; charset=utf-8",  // 이 부분 추가
    		dataType: "json",  // 이 부분 추가
            data: JSON.stringify ({
                coincode: selectedCoinCode,
                money: myPageData.money,
                userBtc: myPageData.userBtc,
                userEth: myPageData.userEth,
                price: pricePer,
                orderAmount: orderAmount
            }),
            success: function(response) {
                console.log("Data updated successfully!", response);
                
                // 서버 응답을 기반으로 클라이언트 상태 업데이트
	            myPageData.money = response.money;
	            myPageData.userBtc = response.userBtc;
	            myPageData.userEth = response.userEth;
	            
	            localStorage.setItem('selectedCoinCode', selectedCoinCode);
                
                // 서버로부터 받은 redirectUrl을 사용하여 페이지를 새로고침하거나 리다이렉트 수행
		        if (response.redirectUrl) {
		            window.location.href = response.redirectUrl;
		        }
            },
            error: function(err) {
                console.error("Failed to update data.", err);
            }
        });

        // 업데이트된 값을 콘솔에 출력합니다 (데모용).
        console.log("Updated myPage data:", myPageData);

        // 여기서 필요한 경우 서버에 업데이트된 데이터를 저장하거나 페이지를 갱신할 수 있습니다.
    });
    
    // 전체수익률
    let totalRate = parseFloat($(".total-rate").text());
    if (totalRate > 0) {
        $(".total-rate").removeClass("color_blue");
        $(".total-rate").addClass("color_red").prepend("+");
    } else if (totalRate < 0) {
        $(".total-rate").removeClass("color_red");
        $(".total-rate").addClass("color_blue");
    } else {
        $(".total-rate").removeClass("color_red");
		$(".total-rate").removeClass("color_blue");
    }

    // 전체 평가손익
    let totalProfit = parseFloat($(".total-profit").text().replace(/,/g, ''));
    if (totalProfit > 0) {
        $(".total-profit").removeClass("color_blue");
        $(".total-profit").addClass("color_red").prepend("+");
    } else if (totalProfit < 0) {
        $(".total-profit").removeClass("color_red");
        $(".total-profit").addClass("color_blue");
    } else {
		$(".total-profit").removeClass("color_red");
		$(".total-profit").removeClass("color_blue");
    }
    
    // 코인별 수익률
    $(".tx_value, .tx_rate").each(function() {
	    let valueText = $(this).text().replace(/,/g, '').replace('%', ''); // 콤마와 % 제거
	    let value = parseFloat(valueText);
	
	    if (value > 0) {
	        $(this).addClass("color_red").text("+" + valueText + (valueText.includes('.') ? '%' : ''));
	    } else if (value < 0) {
	        $(this).addClass("color_blue");
	    } else {
	        $(this).removeClass("color_red").removeClass("color_blue").text(valueText + (valueText.includes('.') ? '%' : ''));
	    }
	});
	
	fetchRankingData('BTC'); // 페이지 로딩 시 BTC 데이터로 테이블을 채웁니다.
	
	$('#sortByBtc').click(function() {
        fetchRankingData('BTC');
    });

    $('#sortByEth').click(function() {
        fetchRankingData('ETH');
    });
    
    // 모의투자 랭킹 데이터
    function fetchRankingData(coinType) {
	    $.ajax({
	        url: "/api/ranking/" + coinType,
	        type: 'GET',
	        success: function(data) {
				// 가격 목록을 가져온 후 updateRankingTable 함수를 호출하도록 수정
            	Promise.all([fetchBtcPriceList(), fetchEthPriceList()]).then(() => {
	                updateRankingTable(data, coinType);
	            });
	        }
	    });
	}
	
	function updateRankingTable(data, coinType) {
	    let tbody = $('#rankingTable tbody');
	    tbody.empty(); // 현재 테이블의 내용을 비웁니다.
	    
        data.forEach((user, index) => {
	        let userCoinAmount, currentPrice, sumTradePrice;
	        
	        if(coinType === 'BTC') {
	            userCoinAmount = user.userBtc;
	            currentPrice = priceLista[0].price;
	            sumTradePrice = user.sumBtcTradePrice;
	        } else if(coinType === 'ETH') {
	            userCoinAmount = user.userEth;
	            currentPrice = priceListb[0].price;
	            sumTradePrice = user.sumEthTradePrice;
	        } else {
	            // 예외 처리 혹은 다른 코인의 처리 로직을 여기에 추가
	        }
	
	        let evaluationAmount = (userCoinAmount * currentPrice).toLocaleString('ko-KR');
	        let profitLoss = ((userCoinAmount * currentPrice) - sumTradePrice).toLocaleString('ko-KR');
	        let profitRate = userCoinAmount === 0 ? '0' : (((userCoinAmount * currentPrice - sumTradePrice) / sumTradePrice) * 100).toFixed(2) + '%';
	        
	        let row = `
	            <tr>
	                <td>${index + 1}</td>
	                <td>${user.username}</td>
	                <td>${evaluationAmount} 원</td>
	                <td>${profitLoss} 원</td>
	                <td>${profitRate}</td>
	            </tr>
	        `;
	        tbody.append(row);
	    });
	}

});
