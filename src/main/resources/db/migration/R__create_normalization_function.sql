create or replace
function normalization(target_text varchar)
returns varchar as $$
declare
  -- 半角・全角
  half_nums constant varchar := '0123456789';
  half_alphabets constant varchar := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
  half_katakana_base constant varchar := 'ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜｦﾝｧｨｩｪｫｯｬｭｮｰ';
  full_nums constant varchar := '０１２３４５６７８９';
  full_alphabets constant varchar := 'ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ';

  -- ひらがな・カタカナ
  hiragana constant varchar := 'あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをんがぎぐげござじずぜぞだぢづでどばびぶべぼぱぴぷぺぽぁぃぅぇぉっゃゅょー';
  katakana constant varchar := 'アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポァィゥェォッャュョー';
  katakana_base constant varchar := 'アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンァィゥェォッャュョー';
  
  -- 削除対象の記号
  removable_symbols constant varchar := ' 　-()[]';
  
  converted_full_katakana varchar;

begin
  converted_full_katakana := target_text;
  -- 半角カタカナを全角に変換
  converted_full_katakana := replace(converted_full_katakana, 'ｶﾞ', 'ガ');
  converted_full_katakana := replace(converted_full_katakana, 'ｷﾞ', 'ギ');
  converted_full_katakana := replace(converted_full_katakana, 'ｸﾞ', 'グ');
  converted_full_katakana := replace(converted_full_katakana, 'ｹﾞ', 'ゲ');
  converted_full_katakana := replace(converted_full_katakana, 'ｺﾞ', 'ゴ');
  converted_full_katakana := replace(converted_full_katakana, 'ｻﾞ', 'ザ');
  converted_full_katakana := replace(converted_full_katakana, 'ｼﾞ', 'ジ');
  converted_full_katakana := replace(converted_full_katakana, 'ｽﾞ', 'ズ');
  converted_full_katakana := replace(converted_full_katakana, 'ｾﾞ', 'ゼ');
  converted_full_katakana := replace(converted_full_katakana, 'ｿﾞ', 'ゾ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾀﾞ', 'ダ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾁﾞ', 'ヂ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾂﾞ', 'ヅ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾃﾞ', 'デ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾄﾞ', 'ド');
  converted_full_katakana := replace(converted_full_katakana, 'ﾊﾞ', 'バ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾋﾞ', 'ビ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾌﾞ', 'ブ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾍﾞ', 'ベ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾎﾞ', 'ボ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾊﾟ', 'パ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾋﾟ', 'ピ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾌﾟ', 'プ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾍﾟ', 'ペ');
  converted_full_katakana := replace(converted_full_katakana, 'ﾎﾟ', 'ポ');
  converted_full_katakana := replace(converted_full_katakana, 'ｳﾞ', 'ヴ');

  converted_full_katakana := translate(converted_full_katakana, half_katakana_base, katakana_base);

  return translate(
    converted_full_katakana,
    concat(full_nums, full_alphabets, hiragana, removable_symbols),
    concat(half_nums, half_alphabets, katakana)
  );
end;
$$ language plpgsql;