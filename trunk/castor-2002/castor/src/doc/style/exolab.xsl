<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- Content Stylesheet                             -->
<!-- Ismael Ghalimi ghalimi@exoffice.com            -->
<!-- Copyright (c) Exoffice Technologies, Inc. 1999 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0">

  <xsl:variable name="color-alpha">#ffffff</xsl:variable>
  <xsl:variable name="color-beta" select="'#666699'"/>
  <xsl:variable name="color-gamma" select="'#424264'"/>
  <xsl:variable name="color-delta" select="'#54547f'"/>
  <xsl:variable name="color-epsilon" select="'#bfbffe'"/>

  <xsl:variable name="color-a" select="'#444466'"/>
  <xsl:variable name="color-b" select="'#4d4d73'"/>
  <xsl:variable name="color-c" select="'#565680'"/>
  <xsl:variable name="color-d" select="'#5e5e8c'"/>
  <xsl:variable name="color-e" select="'#676799'"/>
  <xsl:variable name="color-f" select="'#6f6fa6'"/>
  <xsl:variable name="color-g" select="'#7878b3'"/>
  <xsl:variable name="color-h" select="'#8080bf'"/>
  <xsl:variable name="color-i" select="'#8989cc'"/>
  <xsl:variable name="color-j" select="'#9292d9'"/>
  <xsl:variable name="color-k" select="'#9a9ae6'"/>
  <xsl:variable name="color-l" select="'#a2a2f2'"/>
  <xsl:variable name="color-m" select="'#ababff'"/>

  <xsl:variable name="nbsp" select="'&#xA0;'"/>
<!--
  <xsl:include href="document.xsl"/>
  <xsl:include href="widgets.xsl"/>
-->
  <xsl:template match="/">
    <xsl:variable name="project" select="document(document/@project)/project"/>

    <html><head>
      <meta name="author" content="{document/properties/authors/author/@first-name}"/>
      <title><xsl:value-of select="document/properties/title"/></title>
      <link rel="stylesheet" type="text/css" href="style/default.css"/>
    </head>

    <body bgcolor="{$color-e}" link="#bfbffe" vlink="#bfbffe" alink="#bfbffe" leftmargin="0" topmargin="0">
      <table border="0" cellpadding="0" cellspacing="0" width="100%">

        <tr>
          <td bgcolor="{$color-alpha}" colspan="3">

            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                  <td bgcolor="{$color-beta}" align="left" width="254" rowspan="2"><a href="http://www.exolab.org/"><img src="images/exolab.gif" height="117" width="254" border="0"/></a></td>
                  <td bgcolor="{$color-beta}" align="left" height="11" width="3600">
                    <xsl:apply-templates select="$project/links"/>
                  </td>
                </tr>
                <tr>
                  <td bgcolor="{$color-alpha}" width="*" align="left" valign="top">
                    <xsl:value-of select="$nbsp"/><xsl:value-of select="$nbsp"/>
                    <a href="http://castor.exolab.org"><img src="images/castor.gif" border="0"/></a>
                  </td>
                </tr>

            </table>

          </td>
        </tr>

        <tr>
          <td bgcolor="{$color-beta}" width="120" rowspan="2" valign="top">
            <xsl:if test="document/properties">
              <xsl:attribute name="rowspan">4</xsl:attribute>
            </xsl:if>
            <table>
              <xsl:for-each select="$project/menu">
                <tr>
                  <td align="left" colspan="2">
                    <font color="{$color-alpha}"><xsl:value-of select="@name"/></font>
                  </td>
                </tr>
                <xsl:for-each select="item">
                  <tr>
                    <td width="15" valign="top" align="right"><img src="images/bullets/square-small-white.gif" alt="*" height="12" width="12"/></td>
                    <td><span class="alpha">
                      <font size="2"><a href="{@href}"><xsl:value-of select="@name"/></a></font>
                    </span></td>
                  </tr>
                </xsl:for-each>
              </xsl:for-each>
            </table>
          </td>
          <td bgcolor="{$color-alpha}" width="11" valign="top">
              <img src="images/corners/nw-small.gif" height="11" width="11" valign="top"/>
          </td>
          <td bgcolor="{$color-alpha}" height="45">
              <xsl:value-of select="$nbsp"/><img src="images/bullets/dots.gif" height="11" width="41" valign="top"/><xsl:value-of select="$nbsp"/>
              <font size="4" color="{$color-c}"><b>
                <xsl:value-of select="$project/title"/>
              </b></font>    
          </td>
        </tr>

      <xsl:if test="document/properties">
        <tr>
          <td bgcolor="{$color-alpha}" width="11"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-alpha}" valign="top">
             <xsl:apply-templates select="document/properties"/>
          </td>
        </tr>

        <tr>
          <td bgcolor="{$color-beta}" width="11"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-beta}" valign="top"><xsl:value-of select="$nbsp"/></td>
        </tr>
      </xsl:if>

        <tr>
          <td bgcolor="{$color-alpha}" width="11"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-alpha}">
             <xsl:apply-templates select="document/body"/>
             <br/>
          </td>
        </tr>

        <tr>
          <td bgcolor="{$color-beta}" width="120"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-beta}" width="11"></td>
          <td bgcolor="{$color-beta}" valign="top">
          </td>
        </tr>

        <tr>
          <td bgcolor="{$color-beta}" width="120"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-alpha}" width="11"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-alpha}"><br/>
            <xsl:for-each select="$project/notice">
              <small><xsl:copy-of select="."/></small><br/>
            </xsl:for-each><br/>
          </td>
        </tr>

        <tr>
          <td bgcolor="{$color-beta}" width="120"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-beta}" width="11"><xsl:value-of select="$nbsp"/></td>
          <td bgcolor="{$color-beta}" valign="top">
            <xsl:apply-templates select="$project/links"/>
          </td>
        </tr>

      </table>

    </body>
    </html>
  </xsl:template>

  <xsl:template match="project/links">
    <xsl:for-each select="link">
      <a href="{@href}"><img src="{@image}" alt="{@name}" height="{@height}" width="{@width}" border="0" vspace="5"/></a>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>


