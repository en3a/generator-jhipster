<%#
 Copyright 2013-2018 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see http://www.jhipster.tech/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { NgModule, ElementRef, Renderer } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
<%_ if (enableTranslation) { _%>
import { JhiLanguageService, JhiDataUtils, JhiDateUtils, JhiEventManager, JhiAlertService, JhiParseLinks } from 'ng-jhipster';

import { MockLanguageService, MockLanguageHelper } from './helpers/mock-language.service';
import { JhiLanguageHelper, Principal, AccountService<% if (authenticationType !== 'oauth2') { %>, LoginModalService<% } %><% if (websocket === 'spring-websocket') { %>, <%=jhiPrefixCapitalized%>TrackerService<% } %> } from '../../../main/webapp/app/shared';
<%_ } else { _%>
import { JhiDataUtils, JhiDateUtils, JhiEventManager, JhiAlertService, JhiParseLinks } from 'ng-jhipster';

import { Principal, AccountService<% if (authenticationType !== 'oauth2') { %>, LoginModalService<% } %><% if (websocket === 'spring-websocket') { %>, JhiTrackerService<% } %> } from '../../../main/webapp/app/shared';
<%_ } _%>
import { MockPrincipal } from './helpers/mock-principal.service';
import { MockAccountService } from './helpers/mock-account.service';
import { MockActivatedRoute, MockRouter } from './helpers/mock-route.service';
import { MockActiveModal } from './helpers/mock-active-modal.service';
import { MockEventManager } from './helpers/mock-event-manager.service';
<%_
const tsKeyId = generateTestEntityId(pkType, prodDatabaseType);
_%>

@NgModule({
    providers: [
        DatePipe,
        JhiDataUtils,
        JhiDateUtils,
        JhiParseLinks,
        <%_ if (enableTranslation) { _%>
        {
            provide: JhiLanguageService,
            useClass: MockLanguageService
        },
        {
            provide: JhiLanguageHelper,
            useClass: MockLanguageHelper
        },
        <%_ } _%>
        <%_ if (websocket === 'spring-websocket') { _%>
        {
            provide: <%=jhiPrefixCapitalized%>TrackerService,
            useValue: null
        },
        <%_ } _%>
        {
            provide: JhiEventManager,
            useClass:  MockEventManager
        },
        {
            provide: NgbActiveModal,
            useClass: MockActiveModal
        },
        {
            provide: ActivatedRoute,
            useValue: new MockActivatedRoute({id: <%- tsKeyId %>})
        },
        {
            provide: Router,
            useClass: MockRouter
        },
        {
            provide: Principal,
            useClass: MockPrincipal
        },
        {
            provide: AccountService,
            useClass: MockAccountService
        },
        <%_ if (authenticationType !== 'oauth2') { _%>
        {
            provide: LoginModalService,
            useValue: null
        },
        <%_ } _%>
        {
            provide: ElementRef,
            useValue: null
        },
        {
            provide: Renderer,
            useValue: null
        },
        {
            provide: JhiAlertService,
            useValue: null
        },
        {
            provide: NgbModal,
            useValue: null
        },
    ],
    imports: [HttpClientTestingModule]
})
export class <%=angularXAppName%>TestModule {}
