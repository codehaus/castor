<?xml version="1.0" encoding="ISO-8859-1"?>

<!-- Common Widgets Stylesheet                      -->
<!-- Ismael Ghalimi ghalimi@exoffice.com            -->
<!-- Copyright (c) Exoffice Technologies, Inc. 1999 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/XSL/Transform/1.0">

  <xsl:template match="email">
    <a href="mailto:{.}"><xsl:copy-of select="."/></a>
  </xsl:template>

  <xsl:template match="url">
    <a href="{.}"><xsl:copy-of select="."/></a>
  </xsl:template>

  <xsl:template match="headline">
    <div>
      <span class="small"><xsl:apply-templates/></span>
    </div>
  </xsl:template>

  <xsl:template match="mailing-list">
    <h1><xsl:apply-templates select="title"/></h1>
    <div><xsl:apply-templates select="description"/></div>
    <br/>
    <div>
      [
      <a href="mailto:{@manager}?subject={@subscribe}">Subscribe</a> |
      <a href="mailto:{@manager}?subject={@unsubscribe}">Unsubscribe</a> |
      <a href="mailto:{@post}">Post</a>
      ]
    </div>
  </xsl:template>

  <xsl:template match="mailing-list/title">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="mailing-list/description">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="mailing-lists">
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>