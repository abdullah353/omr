package org.lightcouch.tests;

public class ResultsByAssessmentId {
	private String _id;
	private String _rev;
	public ResultsByAssessmentId() {
		super();
	}

	public ResultsByAssessmentId(String _id) {
		this._id = _id;
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	
}
