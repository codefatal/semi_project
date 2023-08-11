package com.semi.project.model;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedData {
	private Optional<TradeTest> tradeTest;
	private Optional<MyPage> myPage;
	
	public CombinedData(Optional<TradeTest> tradeTest, Optional<MyPage> myPage) {
		this.tradeTest = tradeTest;
		this.myPage = myPage;
	}
}
