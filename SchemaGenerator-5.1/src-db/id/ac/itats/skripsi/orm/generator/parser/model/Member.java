/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package id.ac.itats.skripsi.orm.generator.parser.model;

/**
 *
 * @author Willy Tiengo
 */
public class Member {

    public String type;
    public String ref;
    public String role;

    public Member(String type, String ref, String role) {
        this.type = type;
        this.ref = ref;
        this.role = role;
    }
}
