package org.example.demojavafx;

import java.util.ArrayList;

public class SQLRequest {

    public String stringAndOrOr(ArrayList<String> andOrOrElements, String andOrOr){
        StringBuilder sb = new StringBuilder();
        if (! (andOrOrElements.isEmpty()) ){
            sb.append("(");
            sb.append(andOrOrElements.get(0));
            for (int i = 1; i < andOrOrElements.size(); i++){
                sb.append(" ");
                sb.append(andOrOr);
                sb.append(" ");
                sb.append(andOrOrElements.get(i));
            }
            sb.append(")");
        }
        return sb.toString();
    }
    public String stringConjonction(ArrayList<String> andElements){
        return stringAndOrOr(andElements, "AND");
    }
    public String stringDisjonction(ArrayList<String> orElements){
        return stringAndOrOr(orElements, "OR");
    }

    public String stringConstraint(String champsString, String constraint) {
        StringBuilder sb = new StringBuilder();
        sb.append(champsString);
        sb.append(" == '");
        sb.append(constraint);
        sb.append("'");
        return sb.toString();
    }
    public ArrayList<String> arrayStringConstraint(String champsString, ArrayList<String> constraints){
        ArrayList<String> champsConstraintsArray = new ArrayList<>();
        for (String constraint: constraints){
            champsConstraintsArray.add(stringConstraint(champsString, constraint));
        }
        return champsConstraintsArray;
    }
    public String constraintsToString(String champsString, ArrayList<String> constraintArray) {
        StringBuilder sb = new StringBuilder();
        if (!constraintArray.isEmpty()) {
            sb.append(stringDisjonction(arrayStringConstraint(champsString, constraintArray)));
        }
        return sb.toString();
    }
    public String constraintsYearToString(String champsString, ArrayList<String> constraintArray) {
        StringBuilder sb = new StringBuilder();
        if (!constraintArray.isEmpty()) {
            sb.append(stringDisjonction(arrayStringConstraint(champsString, constraintArray)).replaceAll("'",""));
        }
        return sb.toString();
    }



    public ArrayList<String> userConstraintsToArrayString(ArrayList<String> matiereArray, ArrayList<String> chapitreArray, ArrayList<String> yearArray, ArrayList<String> typeArray) {
        ArrayList<String> userConstraintsArray = new ArrayList<>();

        String matiereConstraints = constraintsToString("matiereNom",  matiereArray);
        if (!matiereConstraints.isEmpty()){
            userConstraintsArray.add(matiereConstraints);
        }

        String chapitreConstraints = constraintsToString("chapitreNom",  chapitreArray);
        if (!chapitreConstraints.isEmpty()){
            userConstraintsArray.add(chapitreConstraints);
        }

        String yearConstraints = constraintsYearToString("year",  yearArray);
        if (!yearConstraints.isEmpty()){
            userConstraintsArray.add(yearConstraints);
        }

        String typeConstraints = constraintsToString("type",  typeArray);
        if (!typeConstraints.isEmpty()){
            userConstraintsArray.add(typeConstraints);
        }
        return userConstraintsArray;
    }


    public String stringUserConstraints(ArrayList<String> matiereArray, ArrayList<String> chapitreArray, ArrayList<String> yearArray, ArrayList<String> typeArray){
        ArrayList<String> usrConstraintsArray = userConstraintsToArrayString(matiereArray, chapitreArray, yearArray, typeArray);
        return stringConjonction(usrConstraintsArray);
    }

    public String stringUserConstraintsAndIsFirstCard(String stringUsrConstraints, String isFirst){
        StringBuilder sb = new StringBuilder();
        if (!stringUsrConstraints.isEmpty()) {
            sb.append("WHERE ( (isFirstOfSuite == '"+ isFirst + "') ");
            sb.append("AND (idChap in (SELECT idChap FROM chapitres WHERE (");
            sb.append(stringUsrConstraints);
            sb.append("))))");
        }
        else {sb.append("WHERE (isFirstOfSuite == '"+ isFirst + "') ");}

        return sb.toString();
    }

    public String stringSelectWhereOrder(String whereConstraints){
        StringBuilder sb = new StringBuilder("SELECT * FROM flashcards ");
        sb.append(whereConstraints);
        sb.append(" ORDER BY lastseen;");
        return sb.toString();
    }

    public String sqlRequest(ArrayList<String> matiereArray, ArrayList<String> chapitreArray, ArrayList<String> yearArray, ArrayList<String> typeArray, String isFirst){
        String strUsrConstraints = stringUserConstraints(matiereArray, chapitreArray, yearArray, typeArray);
        String strUsrConstraintsAndIsFirstCard = stringUserConstraintsAndIsFirstCard(strUsrConstraints, isFirst);
        return stringSelectWhereOrder(strUsrConstraintsAndIsFirstCard);
    }

}
