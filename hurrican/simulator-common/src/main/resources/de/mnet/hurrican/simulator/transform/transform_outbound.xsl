<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- XSL transformation takes care of empty elements that would cause schema validation errors, those elements are removed completely -->
    <xsl:output method="text" omit-xml-declaration="no"/>

    <xsl:template match="node()">
        <xsl:choose>
            <xsl:when test="not(parent::*)">&lt;<xsl:value-of select="name(.)"/><xsl:call-template name="namespaces"/><xsl:call-template name="attributes"/>&gt;<xsl:apply-templates/>&lt;/<xsl:value-of select="name(.)"/>&gt;</xsl:when>
            <xsl:when test="child::*">&lt;<xsl:value-of select="name(.)"/><xsl:call-template name="attributes"/>&gt;<xsl:apply-templates/>&lt;/<xsl:value-of select="name(.)"/>&gt;</xsl:when>
            <xsl:when test="text()">&lt;<xsl:value-of select="name(.)"/><xsl:call-template name="attributes"/>&gt;<xsl:value-of select="text()"/>&lt;/<xsl:value-of select="name(.)"/>&gt;</xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="namespaces">
        <xsl:for-each select="namespace::*">
            <xsl:if test="not(name(.) = 'xml')">
                <xsl:choose>
                    <xsl:when test="string-length(name(.))"><xsl:text> </xsl:text>xmlns:<xsl:value-of select="name(.)"/>=&quot;<xsl:value-of select="."/>&quot;</xsl:when>
                    <xsl:otherwise><xsl:text> </xsl:text>xmlns=&quot;<xsl:value-of select="."/>&quot;</xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="attributes">
        <xsl:for-each select="@*"><xsl:text> </xsl:text><xsl:value-of select="name(.)"/>=&quot;<xsl:value-of select="."/>&quot;</xsl:for-each>
    </xsl:template>

    <xsl:template match="comment()">&lt;!--<xsl:value-of select="."/>--&gt;</xsl:template>
    <xsl:template match="text()"><xsl:value-of select="."/></xsl:template>

</xsl:stylesheet>