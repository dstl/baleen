//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.Vehicle;


/**
 * Annotate flight numbers using a regular expression
 * <p>
 * Flight numbers are extracted using a regular expression.
 * To minimise false positives, the flight number must be preceded by a variation on the words 'Flight Number'.
 * The regular expression validates the flight number against a list of IATA airline codes (pulled from Wikipedia) followed by a number.
 * 
 * 
 */
public class FlightNumber extends AbstractRegexAnnotator<Vehicle> {
	private static final String FLIGHTNUMBER_REGEX = "\\b(Flight|Flight Number|Flight No| Flight #)[: ]+((ZY|ZX|ZW|ZV|ZU|ZT|ZS|ZP|ZL|ZK|ZI|ZH|ZG|ZE|ZB|ZA|Z8|Z7|Z5|Z4|Z3|YX|YW|YV|YU|YT|YS|YM|YL|YK|YH|YE|YD|Y9|Y8|Y6|Y5|Y4|Y2|XT|XS|XQ|XP|XO|XM|XL|XK|XJ|XF|X9|X7|X3|WZ|WY|WX|WW|WV|WT|WS|WR|WO|WN|WK|WH|WG|WF|WE|WD|WC|WB|WA|W9|W8|W6|W5|W4|W3|W2|W1|VZ|VY|VX|VW|VV|VU|VT|VS|VR|VP|VO|VN|VM|VL|VK|VJ|VI|VH|VG|VF|VE|VD|VC|VB|VA|V9|V8|V7|V5|V4|V3|V2|V0|UZ|UY|UX|UU|UT|US|UQ|UP|UO|UN|UM|UL|UI|UH|UG|UF|UE|UD|UB|UA|U8|U7|U6|U5|U4|U3|U2|TZ|TY|TX|TW|TV|TU|TT|TS|TR|TQ|TP|TO|TN|TM|TL|TK|TI|TH|TG|TF|TE|TD|TC|T9|T7|T6|T4|T3|T2|SZ|SY|SX|SW|SV|SU|ST|SS|SR|SQ|SP|SO|SN|SM|SL|SK|SJ|SI|SH|SG|SF|SE|SD|SC|SB|SA|S9|S8|S7|S6|S5|S4|S3|S2|S0|RZ|RX|RW|RV|RU|RS|RR|RQ|RP|RO|RL|RK|RJ|RI|RH|RG|RF|RE|RD|RC|RB|RA|R9|R8|R7|R6|R5|R3|R2|R1|R0|QZ|QY|QX|QW|QV|QU|QT|QS|QR|QQ|QO|QN|QM|QL|QK|QJ|QI|QH|QF|QE|QD|QC|QB|Q9|Q8|Q6|Q5|Q4|Q3|PZ|PY|PX|PW|PV|PU|PT|PS|PR|PQ|PO|PN|PM|PL|PK|PJ|PI|PH|PG|PF|PE|PD|PC|PA|P9|P8|P7|P5|P3|P0|OZ|OY|OX|OW|OV|OU|OT|OS|OR|OP|OO|ON|OM|OL|OK|OJ|OH|OF|OE|OD|OB|OA|O9|O8|O7|O6|O4|O2|NZ|NY|NX|NW|NV|NU|NT|NR|NQ|NO|NN|NM|NL|NK|NI|NH|NG|NF|NE|NC|NB|NA|N9|N8|N7|N6|N5|N4|N3|N2|MZ|MY|MX|MW|MV|MU|MT|MS|MR|MQ|MP|MO|MN|MM|ML|MK|MJ|MI|MH|MG|MF|ME|MD|MC|MB|MA|M9|M8|M7|M6|M5|M3|M2|LZ|LY|LX|LW|LV|LU|LT|LS|LR|LQ|LP|LO|LN|LM|LL|LK|LJ|LI|LH|LG|LF|LD|LC|LB|LA|L9|L8|L7|L6|L5|L4|L3|L2|L1|KZ|KY|KX|KW|KV|KU|KS|KR|KQ|KP|KO|KN|KM|KL|KK|KJ|KI|KG|KF|KE|KD|KC|KB|KA|K9|K8|K6|K5|K4|K2|JZ|JY|JX|JW|JV|JU|JT|JS|JR|JQ|JP|JO|JN|JM|JL|JK|JJ|JI|JH|JF|JE|JC|JB|JA|J9|J8|J7|J6|J4|J3|J2|IZ|IY|IX|IW|IV|IT|IR|IQ|IP|IO|IN|IM|IK|IJ|II|IH|IG|IF|IE|ID|IC|IB|IA|I9|I7|I6|I4|I2|HZ|HY|HX|HW|HV|HU|HT|HR|HQ|HP|HO|HN|HM|HK|HJ|HH|HG|HF|HE|HD|HC|HB|HA|H9|H8|H7|H6|H5|H4|H2|GZ|GY|GX|GW|GV|GT|GS|GR|GQ|GP|GO|GN|GM|GL|GK|GJ|GI|GH|GG|GF|GE|GD|GC|GB|GA|G9|G8|G7|G6|G5|G4|G3|G2|G1|G0|FZ|FY|FX|FW|FV|FT|FS|FR|FP|FO|FN|FM|FL|FK|FJ|FI|FH|FG|FF|FE|FD|FC|FB|FA|F9|F7|F6|F5|F4|F3|F2|EZ|EY|EX|EW|EV|EU|ET|ES|ER|EQ|EP|EO|EN|EM|EL|EK|EJ|EI|EH|EG|EF|EE|ED|EC|EA|E9|E8|E7|E6|E5|E4|E3|E2|E1|E0|DY|DX|DW|DV|DU|DT|DR|DQ|DP|DO|DM|DL|DK|DJ|DI|DH|DG|DE|DD|DC|DB|DA|D9|D8|D7|D6|D5|D4|D3|CZ|CY|CX|CW|CV|CU|CT|CS|CR|CQ|CP|CO|CN|CM|CL|CK|CJ|CI|CH|CG|CF|CE|CD|CC|CB|CA|C9|C8|C7|C6|C5|C4|C3|BZ|BY|BX|BW|BV|BT|BS|BR|BQ|BP|BO|BN|BM|BL|BK|BJ|BI|BH|BG|BF|BE|BD|BC|BB|BA|B9|B8|B6|B5|B4|B3|B2|AZ|AY|AX|AW|AV|AU|AT|AS|AR|AQ|AP|AO|AN|AM|AL|AK|AJ|AI|AH|AF|AE|AD|AC|AB|AA|A9|A8|A7|A6|A5|A4|A3|A2|9Y|9W|9V|9U|9T|9R|9Q|9O|9L|9K|9I|9E|9C|9A|8Z|8Y|8W?|8W|8V|8U|8T|8S|8Q|8P|8O|8N|8M|8L|8J|8I|8H|8F|8E|8D|8C|8B|8A|7W|7T|7S|7R|7O|7N|7M|7L|7K|7G|7F|7E|7C|7B|7A|6Z|6W|6V|6U|6R|6Q|6P|6N|6K|6J|6I|6H|6G|6E|6B|6A|5Z|5Y|5X|5W|5V|5T|5O|5N|5M|5L|5K|5J|5G|5F|5D|5C|5A|4Y|4U|4T|4S|4R|4N|4M|4K|4H|4G|4F|4D|4C|4A|3W|3V|3U|3T|3S|3R|3Q|3P|3N|3L|3K|3J|3G|3C|3B|2Z|2W|2V|2U|2T|2S|2R|2Q|2P|2O|2N|2M|2L|2K|2J|2H|2G|2F|2D|2C|2B|2A|1Z|1Y|1W|1U|1T|1S|1R|1Q|1P|1N|1M|1L|1K|1I|1H|1G|1F|1E|1D|1C|1B|1A|0J|0D|0C|0B|0A)[0-9]+)\\b";

	/**
	 * New instance.
	 */
	public FlightNumber() {
		super(FLIGHTNUMBER_REGEX, 2, false, 1.0f);
	}
	
	@Override
	protected Vehicle create(JCas jCas, Matcher matcher) {
		Vehicle flight = new Vehicle(jCas);
		flight.setSubType("flight");
		flight.setVehicleIdentifier(matcher.group(2));
		return flight;
	}

}
