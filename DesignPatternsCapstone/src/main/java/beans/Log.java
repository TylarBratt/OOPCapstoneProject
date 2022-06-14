package beans;


public class Log {
	public String title;
	public String content;
	public Long id = null;
	public String timestamp = null;
	public Log(Long id, String title, String content, String timestamp) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.timestamp = timestamp;
	}
}
