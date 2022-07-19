package beans;

public enum BidResult {
	BID_SUCCESS("bid-success", "Bid success!"),
	BID_FAILED_NO_VALUE("bid-failed-no-value", "You must enter a bid value!"),
	BID_FAILED_TOO_LOW("bid-failed-too-low", "Bid too low. Try again."),
	BID_FAILED_INVALID_BIDDER("bid-failed-invalid-bidder", "You are not allowed to bid on this item."),
	BID_FAILED_INSUFFICIENT_FUNDS("bid-failed-insufficient-funds", "Bid failed. Insufficient funds.");
	
	public final String paramName;
	public final String displayMessage;
	BidResult(String paramName, String displayMessage){
		this.paramName = paramName;
		this.displayMessage = displayMessage;
	}
}
