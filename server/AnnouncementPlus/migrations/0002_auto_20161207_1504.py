# -*- coding: utf-8 -*-
# Generated by Django 1.10.3 on 2016-12-07 20:04
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('AnnouncementPlus', '0001_squashed_0008_auto_20161202_2354'),
    ]

    operations = [
        migrations.AlterField(
            model_name='announcement',
            name='eventTime',
            field=models.DateTimeField(blank=True, default=b'2000-01-01 00:00:00+00:00'),
        ),
    ]