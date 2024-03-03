DELETE FROM   COMPBEHAVIOR
      WHERE   comp_id IN
                    (SELECT   gc.id
                       FROM   GUICOMPONENT gc JOIN COMPBEHAVIOR cb
                                 ON GC.ID = CB.COMP_ID
                      WHERE   GC.NAME IN
                                 ('tt.neu', 'show.tts4kunde', 'menu.tt', 'open.tt.opentt.action'))
/

DELETE FROM   GUICOMPONENT
      WHERE   name in
                 ('tt.neu', 'show.tts4kunde', 'menu.tt', 'open.tt.opentt.action')
/

