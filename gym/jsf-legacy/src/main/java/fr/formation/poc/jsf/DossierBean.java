package fr.formation.poc.jsf;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Bean JSF de l'écran Valeurs Mobilières (legacy Titres).
 *
 * ATTENTION (formateur) : ce bean contient volontairement 3
 * responsabilités mélangées (métier + formatage UI + persistance).
 * Sert l'atelier M7 (migration JSF→Angular) et l'atelier O27
 * (détection dette tech). Voir DETTE-TECH-INTENTIONNELLE.md.
 */
@ManagedBean(name = "dossierBean")
@ViewScoped
public class DossierBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    private String codeClient;
    private String codeIsin;
    private String sens;
    private Integer quantite;
    private BigDecimal coursLimite;

    private List<DossierVue> dossiers = new ArrayList<>();

    private String totalFraisFormate;
    private String totalAgiosFormate;
    private String dateRechercheFormatee;

    private final NumberFormat formatMonetaire =
            NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private final DateTimeFormatter formatDate =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @PostConstruct
    public void init() {
        rechercher();
    }

    /**
     * Charge la liste des dossiers et calcule les totaux frais/agios.
     * Cette méthode mélange volontairement 3 responsabilités :
     *  1. Persistance (requête EM directe)
     *  2. Logique métier (calcul frais, agios)
     *  3. Formatage UI (NumberFormat, DateTimeFormatter)
     */
    public void rechercher() {
        // Responsabilité 1 — persistance directe (devrait être dans Repository)
        String jpql = "SELECT o FROM OrdreBourse o WHERE 1=1 ";
        if (codeClient != null && !codeClient.isEmpty()) {
            jpql += "AND o.codeClient = '" + codeClient + "' ";
        }
        if (codeIsin != null && !codeIsin.isEmpty()) {
            jpql += "AND o.codeIsin = '" + codeIsin + "' ";
        }
        jpql += "ORDER BY o.dateCreation DESC";

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createQuery(jpql).getResultList();

        // Responsabilité 2 — logique métier (devrait être dans Service)
        BigDecimal totalFrais = BigDecimal.ZERO;
        BigDecimal totalAgios = BigDecimal.ZERO;
        dossiers = new ArrayList<>();
        for (Object[] row : rows) {
            DossierVue v = new DossierVue();
            v.id = (Long) row[0];
            v.codeClient = (String) row[1];
            v.codeIsin = (String) row[2];
            v.sens = (String) row[3];
            v.quantite = (Integer) row[4];
            v.coursLimite = (BigDecimal) row[5];

            BigDecimal brut = v.coursLimite.multiply(new BigDecimal(v.quantite));
            BigDecimal frais;
            if (brut.compareTo(new BigDecimal("1000")) < 0) {
                frais = new BigDecimal("5.00");
            } else if (brut.compareTo(new BigDecimal("10000")) < 0) {
                frais = brut.multiply(new BigDecimal("0.015"));
            } else {
                frais = brut.multiply(new BigDecimal("0.012"));
            }
            frais = frais.setScale(2, RoundingMode.HALF_UP);
            totalFrais = totalFrais.add(frais);

            // Responsabilité 3 — formatage UI (devrait être dans Converter / template)
            v.fraisAffiche = formatMonetaire.format(frais);
            v.coursAffiche = formatMonetaire.format(v.coursLimite);
            dossiers.add(v);
        }

        totalFraisFormate = formatMonetaire.format(totalFrais);
        totalAgiosFormate = formatMonetaire.format(totalAgios);
        dateRechercheFormatee = LocalDateTime.now().format(formatDate);
    }

    public String getCodeClient() { return codeClient; }
    public void setCodeClient(String codeClient) { this.codeClient = codeClient; }
    public String getCodeIsin() { return codeIsin; }
    public void setCodeIsin(String codeIsin) { this.codeIsin = codeIsin; }
    public String getSens() { return sens; }
    public void setSens(String sens) { this.sens = sens; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public BigDecimal getCoursLimite() { return coursLimite; }
    public void setCoursLimite(BigDecimal coursLimite) { this.coursLimite = coursLimite; }
    public List<DossierVue> getDossiers() { return dossiers; }
    public String getTotalFraisFormate() { return totalFraisFormate; }
    public String getTotalAgiosFormate() { return totalAgiosFormate; }
    public String getDateRechercheFormatee() { return dateRechercheFormatee; }

    public static class DossierVue {
        public Long id;
        public String codeClient;
        public String codeIsin;
        public String sens;
        public Integer quantite;
        public BigDecimal coursLimite;
        public String fraisAffiche;
        public String coursAffiche;

        public Long getId() { return id; }
        public String getCodeClient() { return codeClient; }
        public String getCodeIsin() { return codeIsin; }
        public String getSens() { return sens; }
        public Integer getQuantite() { return quantite; }
        public BigDecimal getCoursLimite() { return coursLimite; }
        public String getFraisAffiche() { return fraisAffiche; }
        public String getCoursAffiche() { return coursAffiche; }
    }
}
