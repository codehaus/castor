<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- Document Stylesheet                            -->
<!-- Ismael Ghalimi ghalimi@exoffice.com            -->
<!-- Copyright (c) Exoffice Technologies, Inc. 1999 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0">

  <!-- Process the document -->
  <xsl:template match="document">
    <xsl:apply-templates/>
  </xsl:template>


  <!-- Process the document properties -->
  <xsl:template match="document/properties">
    <table border="0" cellpadding="4" cellspacing="2">
      <tr>
        <td valign="top"><b>Author:</b></td>
        <td valign="top">
          <xsl:for-each select="authors/author">
              <xsl:value-of select="firstname"/>&#xA0;
              <xsl:if test="initials">
                <xsl:value-of select="initials"/>&#xA0;
              </xsl:if>
              <xsl:value-of select="lastname"/>&#xA0;&#xA0;
              <a href="mailto:{@email}"><xsl:value-of select="@email"/></a><br/>
          </xsl:for-each>
        </td>
      </tr>
      <tr>
        <td valign="top"><b>Abstract:</b></td>
        <td valign="top"><xsl:value-of select="abstract"/><br/>&#xA0;&#xA0;</td>
      </tr>
      <tr>
        <td valign="top"><b>Status:</b></td>
        <td valign="top"><xsl:value-of select="status"/></td>
      </tr>
    </table><br/>
  </xsl:template>


  <!-- Process the document body -->
  <xsl:template match="document/body">
    <xsl:if test="/document/properties/title">
      <br/>
      <h1><xsl:value-of select="/document/properties/title"/></h1>
    </xsl:if>
    <xsl:if test="@title">
      <br/>
      <h1><xsl:value-of select="@title"/></h1>
    </xsl:if>

    <small>
    <xsl:for-each select="//section">
      <xsl:if test="@title">
        <xsl:variable name="level" select="count(ancestor::*)"/>
        <xsl:choose>
          <xsl:when test='$level=2'>
            <a href="#{@title}"><xsl:value-of select="@title"/></a><br/>
          </xsl:when>
          <xsl:when test='$level=3'>
            &#xA0;&#xA0;&#xA0;&#xA0;<a href="#{@title}"><xsl:value-of select="@title"/></a><br/>
          </xsl:when>
        </xsl:choose>
      </xsl:if>
    </xsl:for-each>
    </small>
    <br/>

    <xsl:apply-templates/>
  </xsl:template>


  <!-- Process a section in the document. Nested sections are supported -->
  <xsl:template match="document//section">
    <xsl:variable name="level" select="count(ancestor::*)"/>
    <xsl:choose>
      <xsl:when test='$level=2'>
        <a name="{@title}"><h2>
          <xsl:number level="multiple" count="section" format="1.1"/>.&#xA0;&#xA0;<xsl:value-of select="@title"/>
        </h2></a>
      </xsl:when>
      <xsl:when test='$level=3'>
        <a name="{@title}"><h3>
          <xsl:number level="multiple" count="section" format="1.1"/>.&#xA0;&#xA0;<xsl:value-of select="@title"/>
        </h3></a>
      </xsl:when>
      <xsl:when test='$level=4'>
        <a name="{@title}"><h4>
          <xsl:number level="multiple" count="section" format="1.1"/>.&#xA0;&#xA0;<xsl:value-of select="@title"/>
        </h4></a>
      </xsl:when>
      <xsl:when test='$level>=5'>
        <h5><xsl:copy-of select="@title"/></h5>
      </xsl:when>
    </xsl:choose>
    <blockquote>
      <xsl:apply-templates/>
    </blockquote>
  </xsl:template>


  <!-- Paragraphs are separated with one empty line -->
  <xsl:template match="p">
    <p><xsl:apply-templates/><br/></p>
  </xsl:template>


  <!-- UL is processed into a table using graphical bullets -->
  <xsl:template match="ul">
    <table border="0" cellpadding="2" cellspacing="2">
      <tr><td colspan="2" height="5"></td></tr>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="ul/li">
    <tr>
      <td align="left" valign="top" width="15"><img src="images/bullets/blue.gif" height="22" width="15" alt="*"/></td>
      <td align="left" valign="top"><xsl:apply-templates/></td>
    </tr>
  </xsl:template>


  <!-- Substitution for simple project-wide variables that
       may appear in the document
    -->
  <xsl:template match="project-name">
    <xsl:value-of select="$project/@name"/>
  </xsl:template>

  <xsl:template match="project-repository">
    <xsl:value-of select="$project/@repository"/>
  </xsl:template>

  <xsl:template match="project-title">
    <xsl:value-of select="$project/title"/>
  </xsl:template>


  <!-- Everything else in the document is considered HTML and
       produced as such with the proper processing.
   -->
  <xsl:template match="*|@*">
    <xsl:copy>
      <xsl:apply-templates select="*|@*|text()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>








































