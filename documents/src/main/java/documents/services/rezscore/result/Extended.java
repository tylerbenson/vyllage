package documents.services.rezscore.result;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@XmlRootElement(name = "extended", namespace = "")
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public class Extended implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9187090770913890649L;

	@XmlElement(name = "unix_fail")
	private String unix_fail;

	@XmlElement(name = "kincaid_score")
	private Double kincaid_score;

	@XmlElement(name = "ari_score")
	private Double ari_score;

	@XmlElement(name = "colman_liau_score")
	private Double colman_liau_score;

	@XmlElement(name = "flesch_index")
	private String flesch_index;

	@XmlElement(name = "fog_index")
	private Double fog_index;

	@XmlElement(name = "lix_score")
	private Double lix_score;

	@XmlElement(name = "lix_school_year")
	private Double lix_school_year;

	@XmlElement(name = "smog_grading")
	private Double smog_grading;

	@XmlElement(name = "passive_sentence_pct")
	private Double passive_sentence_pct;

	@XmlElement(name = "avg_syllables")
	private Double avg_syllables;

	@XmlElement(name = "sentence_length")
	private Double sentence_length;

	@XmlElement(name = "short_sentence_pct")
	private Double short_sentence_pct;

	@XmlElement(name = "short_sentence_max")
	private Double short_sentence_max;

	@XmlElement(name = "Double_sentence_pct")
	private Double Double_sentence_pct;

	@XmlElement(name = "Double_sentence_min")
	private Double Double_sentence_min;

	@XmlElement(name = "wordcount")
	private Double wordcount;

	@XmlElement(name = "avg_syllable_dev")
	private Double avg_syllable_dev;

	@XmlElement(name = "is_resume_pct")
	private Boolean is_resume_pct;

	@XmlElement(name = "has_linkedin")
	private Boolean has_linkedin;

	@XmlElement(name = "has_objective_section")
	private Boolean has_objective_section;

	@XmlElement(name = "has_references")
	private Boolean has_references;

	@XmlElement(name = "years_experience")
	private Double years_experience;

	@XmlElement(name = "is_working")
	private Double is_working;

	@XmlElement(name = "pct_buzzwords")
	private Double pct_buzzwords;

	@XmlElement(name = "pct_numbers")
	private Double pct_numbers;

	@XmlElement(name = "pct_whitespace")
	private Double pct_whitespace;

	@XmlElement(name = "pct_adverbs")
	private Double pct_adverbs;

	@XmlElement(name = "count_raw")
	private Double count_raw;

	@XmlElement(name = "count_nf")
	private Double count_nf;

	public Extended() {
	}

	public String getUnix_fail() {
		return unix_fail;
	}

	public void setUnix_fail(String unix_fail) {
		this.unix_fail = unix_fail;
	}

	public Double getKincaid_score() {
		return kincaid_score;
	}

	public void setKincaid_score(Double kincaid_score) {
		this.kincaid_score = kincaid_score;
	}

	public Double getAri_score() {
		return ari_score;
	}

	public void setAri_score(Double ari_score) {
		this.ari_score = ari_score;
	}

	public Double getColman_liau_score() {
		return colman_liau_score;
	}

	public void setColman_liau_score(Double colman_liau_score) {
		this.colman_liau_score = colman_liau_score;
	}

	public String getFlesch_index() {
		return flesch_index;
	}

	public void setFlesch_index(String flesch_index) {
		this.flesch_index = flesch_index;
	}

	public Double getFog_index() {
		return fog_index;
	}

	public void setFog_index(Double fog_index) {
		this.fog_index = fog_index;
	}

	public Double getLix_score() {
		return lix_score;
	}

	public void setLix_score(Double lix_score) {
		this.lix_score = lix_score;
	}

	public Double getLix_school_year() {
		return lix_school_year;
	}

	public void setLix_school_year(Double lix_school_year) {
		this.lix_school_year = lix_school_year;
	}

	public Double getSmog_grading() {
		return smog_grading;
	}

	public void setSmog_grading(Double smog_grading) {
		this.smog_grading = smog_grading;
	}

	public Double getPassive_sentence_pct() {
		return passive_sentence_pct;
	}

	public void setPassive_sentence_pct(Double passive_sentence_pct) {
		this.passive_sentence_pct = passive_sentence_pct;
	}

	public Double getAvg_syllables() {
		return avg_syllables;
	}

	public void setAvg_syllables(Double avg_syllables) {
		this.avg_syllables = avg_syllables;
	}

	public Double getSentence_length() {
		return sentence_length;
	}

	public void setSentence_length(Double sentence_length) {
		this.sentence_length = sentence_length;
	}

	public Double getShort_sentence_pct() {
		return short_sentence_pct;
	}

	public void setShort_sentence_pct(Double short_sentence_pct) {
		this.short_sentence_pct = short_sentence_pct;
	}

	public Double getShort_sentence_max() {
		return short_sentence_max;
	}

	public void setShort_sentence_max(Double short_sentence_max) {
		this.short_sentence_max = short_sentence_max;
	}

	public Double getDouble_sentence_pct() {
		return Double_sentence_pct;
	}

	public void setDouble_sentence_pct(Double Double_sentence_pct) {
		this.Double_sentence_pct = Double_sentence_pct;
	}

	public Double getDouble_sentence_min() {
		return Double_sentence_min;
	}

	public void setDouble_sentence_min(Double Double_sentence_min) {
		this.Double_sentence_min = Double_sentence_min;
	}

	public Double getWordcount() {
		return wordcount;
	}

	public void setWordcount(Double wordcount) {
		this.wordcount = wordcount;
	}

	public Double getAvg_syllable_dev() {
		return avg_syllable_dev;
	}

	public void setAvg_syllable_dev(Double avg_syllable_dev) {
		this.avg_syllable_dev = avg_syllable_dev;
	}

	public Boolean getIs_resume_pct() {
		return is_resume_pct;
	}

	public void setIs_resume_pct(Boolean is_resume_pct) {
		this.is_resume_pct = is_resume_pct;
	}

	public Boolean getHas_linkedin() {
		return has_linkedin;
	}

	public void setHas_linkedin(Boolean has_linkedin) {
		this.has_linkedin = has_linkedin;
	}

	public Boolean getHas_objective_section() {
		return has_objective_section;
	}

	public void setHas_objective_section(Boolean has_objective_section) {
		this.has_objective_section = has_objective_section;
	}

	public Boolean getHas_references() {
		return has_references;
	}

	public void setHas_references(Boolean has_references) {
		this.has_references = has_references;
	}

	public Double getYears_experience() {
		return years_experience;
	}

	public void setYears_experience(Double years_experience) {
		this.years_experience = years_experience;
	}

	public Double getIs_working() {
		return is_working;
	}

	public void setIs_working(Double is_working) {
		this.is_working = is_working;
	}

	public Double getPct_buzzwords() {
		return pct_buzzwords;
	}

	public void setPct_buzzwords(Double pct_buzzwords) {
		this.pct_buzzwords = pct_buzzwords;
	}

	public Double getPct_numbers() {
		return pct_numbers;
	}

	public void setPct_numbers(Double pct_numbers) {
		this.pct_numbers = pct_numbers;
	}

	public Double getPct_whitespace() {
		return pct_whitespace;
	}

	public void setPct_whitespace(Double pct_whitespace) {
		this.pct_whitespace = pct_whitespace;
	}

	public Double getPct_adverbs() {
		return pct_adverbs;
	}

	public void setPct_adverbs(Double pct_adverbs) {
		this.pct_adverbs = pct_adverbs;
	}

	public Double getCount_raw() {
		return count_raw;
	}

	public void setCount_raw(Double count_raw) {
		this.count_raw = count_raw;
	}

	public Double getCount_nf() {
		return count_nf;
	}

	public void setCount_nf(Double count_nf) {
		this.count_nf = count_nf;
	}

}
