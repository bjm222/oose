# -*- coding: utf-8 -*-
# Generated by Django 1.10.3 on 2016-12-12 20:14
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('AnnouncementPlus', '0003_auto_20161211_1712'),
    ]

    operations = [
        migrations.AddField(
            model_name='announcement',
            name='cloudKey',
            field=models.CharField(blank=True, default=b'', max_length=64),
        ),
    ]
